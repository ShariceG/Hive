//
//  ServerStatusCode.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 10/4/18.
//  Copyright © 2018 Chuck Onwuzuruike. All rights reserved.
//

import Foundation

enum ServerStatusCode: Int {
    case OK
    case USER_NOT_FOUND
    case USER_ALREADY_EXISTS
    case POST_NOT_FOUND
    case COMMENT_NOT_FOUND
    case UNKNOWN_ERROR
    case UNSUPPORTED_ACTION_TYPE
    case INTERNAL_ERROR
    case NO_LOCATION_PROVIDED
    case INVALID_LOCATION
    
    static func enumFromString(string:String) -> ServerStatusCode? {
        var i = 0
        while let item = ServerStatusCode(rawValue: i) {
            if String(describing: item) == string { return item }
            i += 1
        }
        return nil
    }
}
