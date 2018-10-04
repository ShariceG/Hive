//
//  UtilityBelt.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 10/4/18.
//  Copyright © 2018 Chuck Onwuzuruike. All rights reserved.
//

import Foundation

class UtilityBelt {
    
    enum UtilityBeltError: Error {
        case expectedTrueStatement
    }
    
    public func checkStringNotEmpty(str: String) {
        trueOrFail(truth: !str.isEmpty)
    }
    
    public func trueOrFail(truth: Bool) {
        if (!truth) {
            fatalError("Expected true statement.")
        }
    }
    
}
