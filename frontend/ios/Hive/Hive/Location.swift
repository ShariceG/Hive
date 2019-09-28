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
    public var area: String
    public var latStr: String
    public var lonStr: String
    public private(set) var json: [String: Any]
        
    init() {
        location = CLLocation(latitude: 0, longitude: 0)
        latStr = ""
        lonStr = ""
        area = ""
        json = [String: Any]()
    }

    init(locationJson: [String: Any]) {
        self.json = locationJson
        self.latStr = locationJson["latitude"] as! String
        self.lonStr = locationJson["longitude"] as! String
        self.area = locationJson["area"] as! String
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
        json["area"] = area
        return json
    }
}
