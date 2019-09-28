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

extension Date {
    func getCurrentTimeMs()-> Double {
        return self.timeIntervalSince1970 * 1000
    }
    
    func getCurrentTimeSec()-> Double {
        return self.timeIntervalSince1970
    }
}

extension UIViewController: PostViewDelegate {
    func commentButtonClick(postView: PostView) {
        self.performSegue(withIdentifier: "seeCommentsSegue", sender: postView)
    }
    
    func hideKeyboardWhenTapped() {
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(UIViewController.dismissKeyboard))
        tap.cancelsTouchesInView = false
        view.addGestureRecognizer(tap)
    }
    
    @objc func dismissKeyboard() {
        view.endEditing(true)
    }
    
    func getTestLocation() -> Location {
//        return "47.608013:-122.335167"  // Seattle
//        return "33.844847:-116.549069"
        let location = Location()
        location.latStr = "47.608013"
        location.lonStr = "-122.335167"
        location.area = "Seattle,WA"
        return location
    }
    
    func getTestUser() -> String {
        return "user1"
    }
}
