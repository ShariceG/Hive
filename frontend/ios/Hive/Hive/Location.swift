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
    
    private(set) var EMPTY_LABEL: String = "Somewhere On Earth"
    private(set) var NOT_FOUND_LABEL: String = "???"
    
    init() {
        location = CLLocation(latitude: 0, longitude: 0)
        label = self.EMPTY_LABEL
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
        while true {
            if (self.geoCoordinatesAreReversed()) {
                return
            }
        }
    }
    
    private func geoCoordinatesAreReversed() -> Bool {
        print("LMAO checking: " + self.label)
        return self.label != self.EMPTY_LABEL
    }
    
    private func reverseGeoLocation() {
        CLGeocoder().reverseGeocodeLocation(location) { (placemarks, error) in
            if (error == nil) {
                var loc: String = ""
                if (placemarks?[0].locality == nil || placemarks?[0].administrativeArea == nil) {
                    loc = self.NOT_FOUND_LABEL
                } else {
                    loc = (placemarks?[0].locality!)! + ", " + (placemarks?[0].administrativeArea!)!
                }
                self.label = loc
            } else {
                self.label = self.NOT_FOUND_LABEL
            }
        }
    }
    
}
