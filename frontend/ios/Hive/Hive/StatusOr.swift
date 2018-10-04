//
//  StatusOr.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 10/4/18.
//  Copyright © 2018 Chuck Onwuzuruike. All rights reserved.
//

import Foundation

class StatusOr<T> {
    
    private(set) var errorMessage: String?
    private(set) var error: StatusError?
    private(set) var value: T?
    
    init(v: T?, err: StatusError?, msg: String?) {
        self.value = v
        self.error = err == nil ? StatusError.NO_ERROR : err
        self.errorMessage = msg == nil ? "" : msg
    }
    
    convenience init(err: StatusError?, msg: String?) {
        self.init(v: Optional.none, err: err, msg: msg)
    }
    
    convenience init(err: StatusError?) {
        self.init(v: Optional.none, err: err, msg: Optional.none)
    }
    
    convenience init(v: T?) {
        self.init(v: v, err: Optional.none, msg: Optional.none)
    }
    
    public func get() -> T {
        if (self.value == nil) {
            fatalError("No value to get from. Error: " + getErrorMessage())
        }
        return self.value!
    }
    
    public func hasError() -> Bool {
        // Error is never nil... but sometimes you just never know.
        return error != nil || error! != StatusError.NO_ERROR
    }
    
    public func getStatusError() -> StatusError {
        return error!
    }
    
    public func getErrorMessage() -> String {
        return String(describing: getStatusError()) + " : " + errorMessage!
    }
    
    public var description: String {
        if (hasError()) {
            return getErrorMessage()
        }
        return value.debugDescription
    }
}
