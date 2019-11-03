//
//  ActionType.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 10/4/18.
//  Copyright © 2018 Chuck Onwuzuruike. All rights reserved.
//

import Foundation

enum ActionType : Int {
    case NO_ACTION = 0
    case LIKE = 1
    case DISLIKE = 2
    
    public static func FromString(str: String) -> ActionType {
        switch str {
        case "NO_ACTION":
            return NO_ACTION
        case "LIKE":
            return LIKE
        case "DISLIKE":
            return DISLIKE
        default:
            print("ERROR: Unknown str: ", str)
            return NO_ACTION
        }
    }
    
    public static func ToString(actionType: ActionType) -> String {
        switch actionType {
        case NO_ACTION:
            return "NO_ACTION"
        case LIKE:
            return "LIKE"
        case DISLIKE:
            return "DISLIKE"
        }
    }
}
