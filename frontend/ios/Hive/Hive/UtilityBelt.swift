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
    
    public static func checkStringNotEmpty(str: String) {
        trueOrFail(truth: !str.isEmpty)
    }
    
    public static func trueOrFail(truth: Bool) {
        if (!truth) {
            fatalError("Expected true statement.")
        }
    }
    
    public static func pluralOrSingular(num: Int, word: String) -> String {
        if num == 1 {
            return String(word.dropLast())
        }
        return word
    }
    
    public static func isValidEmail(email: String) -> Bool {
        return !email.contains(" ") && email.contains("@") && email.contains(".")
    }
    
    public static func isValidUsername(username: String) -> Bool {
        return !username.isEmpty && !username.contains(where: {
            return !$0.isLetter && !$0.isNumber
        })
    }
}
