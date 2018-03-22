package com.example.adm.appservicios.Common;

import com.example.adm.appservicios.Remote.IGoogleAPI;
import com.example.adm.appservicios.Remote.RetrofitClient;

/**
 * Created by Adm on 21/03/2018.
 */

public class Common {

    public static final String baseURL = "https://maps.googleapis.com";

    public static IGoogleAPI getGoogleAPI()
    {
        return RetrofitClient.getClient(baseURL).create(IGoogleAPI.class);
    }
}
