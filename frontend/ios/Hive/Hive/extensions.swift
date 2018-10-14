//
//  extensions.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 10/8/18.
//  Copyright © 2018 Chuck Onwuzuruike. All rights reserved.
//

import Foundation
import UIKit

// This file should only contain extensions to built-in types.
// Do not include extensions to custom types here.

extension UIView {
    class func loadFromNibNamed(nibNamed: String, bundle: Bundle? = nil) -> UIView? {
        return UINib(
            nibName: nibNamed,
            bundle: bundle
            ).instantiate(withOwner: nil, options: nil)[0] as? UIView
    }
    
    func copyView() -> UIView? {
        return NSKeyedUnarchiver.unarchiveObject(with: NSKeyedArchiver.archivedData(withRootObject: self)) as? UIView
    }
    
    func timestampToDate(timestampSec: Decimal) -> String {
        let date = Date(timeIntervalSince1970: TimeInterval(Decimal.toFloat(dec: timestampSec)))
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "MM:dd:yyyy HH:mm"
        return dateFormatter.string(from: date)
    }
}

extension Decimal {
    static func toFloat(dec: Decimal) -> Float {
        return (dec as NSNumber).floatValue;
    }
}
