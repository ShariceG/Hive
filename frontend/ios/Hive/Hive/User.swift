//
//  User.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 10/4/18.
//  Copyright © 2018 Chuck Onwuzuruike. All rights reserved.
//

import Foundation

class User {
    
    private(set) var username: String
    private(set) var phoneNumber: String
    private(set) var location: String
    
    init(username: String, phoneNumber: String, location: String) {
        self.username = username
        self.phoneNumber = phoneNumber
        self.location = location
    }
    
    init(username: String, phoneNumber: String) {
        self.username = username
        self.phoneNumber = phoneNumber
        self.location = ""
    }
}
