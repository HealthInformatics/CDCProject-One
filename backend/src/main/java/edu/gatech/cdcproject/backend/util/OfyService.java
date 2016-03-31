package edu.gatech.cdcproject.backend.util;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

import edu.gatech.cdcproject.backend.model.FoodImage;

/**
 * Created by guoweidong on 2/13/16.
 */
public class OfyService {
    static {
        ObjectifyService.register(FoodImage.class);
    }
    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }

    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
}
