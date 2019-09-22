//
//  Location.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 12/30/18.
//  Copyright © 2018 Chuck Onwuzuruike. All rights reserved.
//

import Foundation
import CoreLocation

class Location {
    
    public private(set) var location: CLLocation
    public private(set) var label: String
    public private(set) var locationStr: String
    public private(set) var latStr: String
    public private(set) var lonStr: String
    
    private(set) var NOT_FOUND_LABEL: String = "???"
    
    init() {
        location = CLLocation(latitude: 0, longitude: 0)
        label = ""
        locationStr = ""
        latStr = ""
        lonStr = ""
    }

    init(locationStr: String, label: String) {
        if (!locationStr.contains(":")) {
            print("Invalid locationStr, no ':'")
        }
        self.locationStr = locationStr
        let split = locationStr.split(separator: ":")
        
        if (split.count != 2) {
            print("Invalid locationStr")
        }
        self.latStr = split[0].description
        self.lonStr = split[1].description
        let lat: Double = Double(latStr) ?? 0
        let lon: Double = Double(lonStr) ?? 0
        self.location = CLLocation(latitude: lat, longitude: lon)
        self.label = label
    }
    
    convenience init(locationStr: String) {
        self.init(locationStr: locationStr, label: "")
        self.reverseGeoLocation()  // Fills in label.
    }
    
    public func overrideLabel(newLabel: String) {
        self.label = newLabel
    }
    
    public func waitUntilGeoLocationIsReversed() {
        while self.label.isEmpty {
        }
    }

    private func reverseGeoLocation() {
        print("Reversing geo location")
        CLGeocoder().reverseGeocodeLocation(location) { (placemarks, error) in
            if (error == nil) {
                if (placemarks?[0].locality != nil && placemarks?[0].administrativeArea != nil) {
                    print("Found reverse geo: " + placemarks.debugDescription)
                    self.label = (placemarks?[0].locality!)! + ", " + (placemarks?[0].administrativeArea!)!
                    return
                } else {
                    print("Could not find reverse geo for " + self.locationStr)
                }
            } else {
                print("Error trying to reverse geo for " + self.locationStr + "\nError: " + error.debugDescription)
            }
            self.label = self.NOT_FOUND_LABEL
        }
        waitUntilGeoLocationIsReversed()
    }
    
}
