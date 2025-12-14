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
const val ROUT_USER_DASHBOARD = "user_dashboard" // ADDED: Main entry point for customers

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
// Global App Scaffold
// -------------------------------
const val ROUT_MAIN_APP = "main_app_scaffold"

// -------------------------------
// Tracking
// -------------------------------
const val ROUT_TRACKING = "tracking" // Renamed from parameterized to static route for the main dashboard access

// -------------------------------
// Route Builders for Parameters
// -------------------------------
fun routeParcelDetails(parcelId: String) = "$ROUT_PARCEL_DETAILS/$parcelId"
fun routeDriverParcelDetails(parcelId: String) = "$ROUT_DRIVER_PARCEL_DETAILS/$parcelId"
fun routeDeliveryDetails(deliveryId: String) = "$ROUT_DELIVERY_DETAILS/$deliveryId"
fun routeDeliverySummary(parcelId: String) = "$ROUT_DELIVERY_SUMMARY/$parcelId"
// REMOVED: routeTracking(parcelId) builder, as the tracking screen (UserDashboardScreen) handles input.