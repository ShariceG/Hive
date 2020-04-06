//
//  User.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 10/4/18.
//  Copyright © 2018 Chuck Onwuzuruike. All rights reserved.
//

import Foundation

struct User: Codable {
    
    public var username: String
    public var email: String
    public var isSignUpVerified: Bool
    
    init(username: String, email: String, isSignUpVerified: Bool) {
        self.username = username
        self.email = email
        self.isSignUpVerified = isSignUpVerified
    }
}
