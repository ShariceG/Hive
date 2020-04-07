//
//  Global.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 4/6/20.
//  Copyright © 2020 Chuck Onwuzuruike. All rights reserved.
//

import Foundation

class Environment: NSObject {
    
    public var user: User?
    public var locationHandler: LocationHandler?
    
    override init() {
        super.init()
        user = nil
        locationHandler = nil
    }
    
    static func createGlobalEnvironment() {
        Global.environment = Environment()
    }
    
    func setUser(user: User) {
        self.user = user
    }
    
    // NoOp if called more than once.
    func initLocationHandler() {
        if (locationHandler != nil) {
            return
        }
        locationHandler = LocationHandler()
        locationHandler!.start()
    }
    
    func setLocationHandlerDelegate(delegate: LocationHandlerDelegate) {
        self.locationHandler!.setHandlerDelegate(delegate: delegate)
    }

}
