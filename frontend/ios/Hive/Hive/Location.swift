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
    
    class Area : Equatable {
        public var latStr: String
        public var lonStr: String
        public var city: String
        public var state: String
        public var country: String
        
        init() {
            latStr = ""
            lonStr = ""
            city = ""
            state = ""
            country = ""
        }
        
        func toJson() -> [String: Any] {
            var json = [String: Any]()
            json["latitude"] = latStr
            json["longitude"] = lonStr
            json["city"] = city
            json["state"] = state
            json["country"] = country
            return json
        }
        
        func toString() -> String {
            return city + ", " + country
        }
        
        static func ==(lhs: Area, rhs: Area) -> Bool {
            return lhs.latStr == rhs.latStr && lhs.lonStr == rhs.lonStr
        }
    }
    
    public private(set) var location: CLLocation
    public var area: Area
    public var latStr: String
    public var lonStr: String
        
    init() {
        location = CLLocation(latitude: 0, longitude: 0)
        latStr = ""
        lonStr = ""
        area = Area()
    }

    init(locationJson: [String: Any]) {
        self.latStr = locationJson["latitude"] as! String
        self.lonStr = locationJson["longitude"] as! String
        self.area = Area()
        if locationJson["area"] != nil {
            let lJson = locationJson["area"] as! [String: Any]
            self.area.latStr = lJson["latitude"] as! String
            self.area.lonStr = lJson["longitude"] as! String
            self.area.city = lJson["city"] as! String
            self.area.state = lJson["state"] as! String
            self.area.country = lJson["country"] as! String
        }
        let lat: Double = Double(latStr) ?? 0
        let lon: Double = Double(lonStr) ?? 0
        self.location = CLLocation(latitude: lat, longitude: lon)
    }
    
    static func jsonToLocation(json: [String: Any]) -> Location {
        return Location(locationJson: json)
    }
    
    func toJson() -> [String: Any] {
        var json = [String: Any]()
        json["latitude"] = latStr
        json["longitude"] = lonStr
        json["area"] = area.toJson()
        return json
    }
}
