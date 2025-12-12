package com.mervyn.ggcouriergo.navigation

// -------------------------------
// Auth & Intro
// -------------------------------
const val ROUT_SPLASH_SCREEN = "splash"
const val ROUT_ONBOARDING = "onboarding"
const val ROUT_LOGIN = "login"
const val ROUT_REGISTER = "register"

// -------------------------------
// Dashboards
// -------------------------------
const val ROUT_DRIVER_DASHBOARD = "driver_dashboard"
const val ROUT_DISPATCHER_DASHBOARD = "dispatcher_dashboard"
const val ROUT_ADMIN_DASHBOARD = "admin_dashboard"

// -------------------------------
// Parcel / Delivery
// -------------------------------
const val ROUT_CREATE_PARCEL = "create_parcel"
const val ROUT_PARCEL_DETAILS = "parcel_details"
const val ROUT_DRIVER_PARCEL_DETAILS = "driver_parcel_details"

const val ROUT_DELIVERY_DETAILS = "delivery_details"
const val ROUT_DELIVERY_SUMMARY = "driver_delivery_summary"

// -------------------------------
// Driver shift
// -------------------------------
const val ROUT_DRIVER_SHIFT = "driver_shift"

// -------------------------------
// Profile & Settings
// -------------------------------
const val ROUT_PROFILE = "profile"
const val ROUT_SETTINGS = "settings"

// -------------------------------
// Tracking
// -------------------------------
const val ROUT_TRACKING = "tracking"

// -------------------------------
// Route Builders for Parameters
// -------------------------------
fun routeParcelDetails(parcelId: String) = "$ROUT_PARCEL_DETAILS/$parcelId"
fun routeDriverParcelDetails(parcelId: String) = "$ROUT_DRIVER_PARCEL_DETAILS/$parcelId"
fun routeDeliveryDetails(deliveryId: String) = "$ROUT_DELIVERY_DETAILS/$deliveryId"
fun routeDeliverySummary(parcelId: String) = "$ROUT_DELIVERY_SUMMARY/$parcelId"
fun routeTracking(parcelId: String) = "$ROUT_TRACKING/$parcelId"
fun routeDriverDashboard(driverId: String) = "$ROUT_DRIVER_DASHBOARD/$driverId"
