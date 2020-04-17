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

extension UIViewController{
        
    func showPermanentAlert(message: String) {
        showPermanentAlert(title: "", message: message)
    }
    
    func showPermanentAlert(title: String, message: String) {
        DispatchQueue.main.async {
            let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
            self.presentAlert(alert: alert)
        }
    }
    
    func dismissAlert() {
        if presentedViewController == nil {
            return
        }
        let controller = presentedViewController!
        if controller .isKind(of: UIAlertController.self) {
            controller.dismiss(animated: true, completion: nil)
        }
    }
  
    func showInternalServerErrorAlert() {
        showAlert(title: "Um... Yikes", message: "Some server error.")
    }
    
    func showAlert(message: String) {
        showAlert(title: "", message: message)
    }
    
    func showAlert(title: String, message: String) {
        DispatchQueue.main.async {
            let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
            alert.addAction(.init(title: "Ok", style: .cancel, handler: { action in
                self.enableUserActivity()
            }))
            self.presentAlert(alert: alert)
        }
    }
    
    func presentAlert(alert: UIAlertController) {
        self.present(alert, animated: true, completion: nil)
    }
    
    func disableUserActivity() {
        self.view.isUserInteractionEnabled = false
    }
    
    func enableUserActivity() {
        self.view.isUserInteractionEnabled = true
    }
    
    func newViewController(storyboardName: String, storyboardId: String) -> UIViewController {
        return UIStoryboard(name: storyboardName, bundle: nil) .
            instantiateViewController(withIdentifier: storyboardId)
    }
    
    func hideKeyboardWhenTapped() {
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(UIViewController.dismissKeyboard))
        tap.cancelsTouchesInView = false
        view.addGestureRecognizer(tap)
    }
    
    @objc func dismissKeyboard() {
        view.endEditing(true)
    }
    
    func getCurrentUserLocation() -> Location {
        return Location(loc: Global.environment!.locationHandler!.getCurrentLocation())
    }
    
    func getLoggedInUsername() -> String {
        return Global.environment!.user!.username
    }
}
