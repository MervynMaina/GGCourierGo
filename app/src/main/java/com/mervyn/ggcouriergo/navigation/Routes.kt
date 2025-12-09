package com.mervyn.ggcouriergo.navigation


// Core Screens
const val ROUT_ONBOARDING = "onboarding"
const val ROUT_SPlASH_SCREEN = "splash"
const val ROUT_LOGIN = "login"
const val ROUT_REGISTER = "register"
const val ROUT_DRIVER_DASHBOARD = "driver_dashboard"
const val ROUT_DISPATCHER_DASHBOARD = "dispatcher_dashboard"

// Parcel / Delivery Screens
const val ROUT_CREATE_PARCEL = "create_parcel"
const val ROUT_PARCEL_DETAILS = "parcel_details/{parcelId}"
const val ROUT_DRIVER_PARCEL_DETAILS = "driver_parcel_details/{parcelId}"
const val ROUT_DELIVERY_DETAILS = "delivery_details/{deliveryId}"

// Helper functions for routes with parameters
fun parcelDetailsRoute(parcelId: String) = "parcel_details/$parcelId"
fun driverParcelDetailsRoute(parcelId: String) = "driver_parcel_details/$parcelId"
fun deliveryDetailsRoute(deliveryId: String) = "delivery_details/$deliveryId"
