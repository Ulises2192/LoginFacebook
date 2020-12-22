package com.ulisesdiaz.loginfacebook

/****
 * Project: LoginFacebook
 * From: com.ulisesdiaz.loginfacebook
 * Created by: Ulises Diaz on 21/12/20 ar 09:55 PM
 * All rights reserved 2020
 ****/
class PictureResponse {

    var picture: PictureDataResponse? = null
}

class PictureDataResponse{
    var data: DataObject? = null
}

class DataObject{
    var height: Int = 0
    var width: Int = 0
    var url: String? = ""
}