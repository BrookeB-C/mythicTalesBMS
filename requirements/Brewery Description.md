# Brewery Description

A brewery is made of of one or more production facilities and one or more taprooms. A production facility will have one or more brew systems and a taproom will have one or more taplists. A production facility will have 1 or more fermentors that can be scheduled.

A Brewery will have one or more Brewers and one may be assigned as Head Brewer. A taproom will have a taproom manager and some number of beer tenders. A single person may hold all rolls.

A Brewery has one or more kegs and those kegs can be transferred to a connected Taproom, and External Tap Room(in-system), A Bar (in-system), an external Venue or a distributor(external). There should be a way to track the external entities that kegs are transferred to, the keg lifecycle for kegs transferred to external will got from Distributed to Returned.

A brew system is diferentiated by its capacity and may be listed in gallons/liters or barrels.

A Brewery has a catalog of recipes, the same recipe can exist in multiple Breweries catalog as a separate entity in the backend database.

A production run is started by selecting a recipe for the run, or creating a new recipe. The recipe may be changed the changes should be saved to the current production run but the brewer should have the opportunity to save it to the catalog either as a new recipe or a replacement for the original recipe. The production run will also include choosing the brew system to be used, which may require scaling the recipe. If there isn't an appropriate fermentor available for when the brewing is finished a warning should be thrown, other wise they should choose the fermentor at the time the run is created. A production run should include a start date/time so the availability of brew system and fermentors can be verified.

When a production run is started a shopping list should be created against the current production inventory.