//
//  StatusError.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 10/4/18.
//  Copyright © 2018 Chuck Onwuzuruike. All rights reserved.
//

import Foundation

enum StatusError: Int {
    case NO_ERROR = 0
    case CONNECTION_TIMEOUT_ERROR = 1
    case GENERIC_CONNECTION_ERROR = 2
    // Use in a context where you expect something to not be empty is empty
    case GENERIC_EMPTY_ERROR = 3
    case GENERIC_SERVER_ERROR = 4
    // Use in a context where you expect something to be 100% true but it isn't
    case GENERIC_INVARIANT_ERROR = 5
    case SERVER_NOT_FOUND = 6
}
