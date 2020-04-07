//
//  LocationManager.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 4/5/20.
//  Copyright © 2020 Chuck Onwuzuruike. All rights reserved.
//

import Foundation
import CoreLocation
import UIKit

protocol LocationHandlerDelegate: class {
    func userDeniedLocationPermission()
    func userApprovedLocationPermission()
    func locationUpdate(location: CLLocation)
}

class LocationHandler : NSObject, CLLocationManagerDelegate {
    
    private var manager: CLLocationManager
    private weak var handlerDelegate: LocationHandlerDelegate?
    
    override init() {
        manager = CLLocationManager()
        handlerDelegate = nil
    }
    
    func start() {
        print("Starting location handler...")
        manager.distanceFilter = 1600
        manager.activityType = .fitness
        manager.desiredAccuracy = kCLLocationAccuracyKilometer
        manager.delegate = self
        
        if !requestAuthorizationIfNeeded() {
            manager.startUpdatingLocation()
        }
    }
    
    func requestAuthorizationIfNeeded() -> Bool {
        let authStatus = CLLocationManager.authorizationStatus()
        if authStatus != .authorizedWhenInUse || authStatus != .authorizedAlways {
            manager.requestWhenInUseAuthorization()
            return true
        }
        return false
    }
    
    func requestAuthorizationOnForegroundIfNeeded() -> Bool {
        let authStatus = CLLocationManager.authorizationStatus()
        if authStatus != .authorizedWhenInUse || authStatus != .authorizedAlways {
            if handlerDelegate != nil {
                handlerDelegate?.userDeniedLocationPermission()
            }
            return true
        }
        return false
    }
    
    func setHandlerDelegate(delegate: LocationHandlerDelegate) {
        handlerDelegate = delegate
    }
    
    func getCurrentLocation() -> CLLocation {
        let location = manager.location
        if location == nil {
            print("ERROR: no location. YOU SHOULD NEVER SEE THIS. If you do, we never got location services properly enabled.")
        } else {
            return location!
        }
        return CLLocation()
    }
    
    func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
        switch status {
        case .authorizedWhenInUse, .authorizedAlways:
            print("Got user authorization.")
            manager.startUpdatingLocation()
            if handlerDelegate != nil {
                handlerDelegate?.userApprovedLocationPermission()
            }
            break;
        case .denied, .restricted:
            if handlerDelegate != nil {
                handlerDelegate?.userDeniedLocationPermission()
            }
            break;
        case .notDetermined:
            manager.requestWhenInUseAuthorization()
            break;
        default: break
        }
    }
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        if handlerDelegate != nil {
            handlerDelegate!.locationUpdate(location: locations.last!)
        }
    }
}
