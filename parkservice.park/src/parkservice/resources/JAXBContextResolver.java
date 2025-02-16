package parkservice.resources;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.api.json.JSONJAXBContext;

import parkservice.gridservice.model.FindGridsByGPSCoordinateResponse;
import parkservice.gridservice.model.GetSpotLevelInfoResponse;
import parkservice.gridservice.model.GetUpdatedStreetInfoResponse;
import parkservice.gridservice.model.SearchForStreetsResponse;
import parkservice.model.ParkResponse;
import parkservice.model.RefillResponse;
import parkservice.model.UnparkResponse;


@Provider
public class JAXBContextResolver implements ContextResolver<JAXBContext>{

			private final JAXBContext context;
			private final Class[] cTypes = {ParkResponse.class, RefillResponse.class, UnparkResponse.class, 
					FindGridsByGPSCoordinateResponse.class, GetSpotLevelInfoResponse.class, 
					GetUpdatedStreetInfoResponse.class, SearchForStreetsResponse.class};
			private final Set<Class> types;
			
			public JAXBContextResolver() throws JAXBException {
				this.context = new JSONJAXBContext (JSONConfiguration.natural().build(), cTypes);
				this.types= new HashSet(Arrays.asList(cTypes));
			}
	
			public JAXBContext getContext(Class<?> objectType) {
				return (types.contains(objectType)) ? context : null;
			}
	
}
