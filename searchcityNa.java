@PostMapping("/es/searchNearByCityName")
	public Map<Integer,String> searchNearByCityName(@RequestParam("lat") Float lat, @RequestParam("lon") Float lon,
			@RequestParam("dis") Integer dis) {
		String distance = dis.toString() + "km";
		SearchResponse searchResponse = client.prepareSearch(ES_INDEX_NAME).setTypes(ES_TYPE_LOCATION)
				.setQuery(QueryBuilders.boolQuery().filter(QueryBuilders.termQuery("type", "city"))
						.filter(QueryBuilders.geoDistanceQuery("location").point(lat, lon).distance(distance)))
				.addSort(SortBuilders.geoDistanceSort("location", lat, lon).order(SortOrder.ASC)
						.unit(DistanceUnit.KILOMETERS).geoDistance(GeoDistance.ARC))
				.get();
		
		Map<Integer,String> result = new HashMap<>();
		for (SearchHit searchHit : searchResponse.getHits().getHits()) {
			Map<String,Object> city = searchHit.getSource();
			city.put("lat", ((Map) city.get("location")).get("lat"));
			city.put("lon", ((Map) city.get("location")).get("lon"));
			city.remove("location");
			city.remove("variations");
			result.put((JSONUtils.getObjectFromMap(city, Locations.class)).getId(),(JSONUtils.getObjectFromMap(city,Locations.class)).getName());
			}
		System.out.println(result.toString());
		return result;
	}
