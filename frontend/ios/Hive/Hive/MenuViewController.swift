//
//  MenuViewController.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 9/21/19.
//  Copyright © 2019 Chuck Onwuzuruike. All rights reserved.
//

import Foundation
import UIKit

class MenuViewController: UIViewController {
    
    let MAP_VIEW_SEGUE_IDENTIFIER = "seeMapViewSegue"
    let SIGN_IN_PAGE_VIEW_SEGUE_IDENTIFIER =
        "GoToSignInSeugue"
    
    @IBOutlet weak var usernameLabel: UILabel!
    @IBOutlet weak var mapButton: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupSwipeGestures()
        usernameLabel.text = "User: " + getLoggedInUsername()
    }
    
    private func setupSwipeGestures() {
        let swipeDown = UISwipeGestureRecognizer(
            target: self, action: #selector(swipeGestureAction))
        swipeDown.direction = .down
        
        self.view?.addGestureRecognizer(swipeDown)
    }
    
    @objc func swipeGestureAction(recognizer: UISwipeGestureRecognizer) {
        switch recognizer.direction {
        case UISwipeGestureRecognizer.Direction.down:
            _ = self.navigationController?.popViewController(animated: true)
            self.dismiss(animated: true, completion: nil)
        default:
            break
        }
    }
    
    func deleteUserFromDefaults() {
        UserDefaults.standard.removeObject(forKey: "user")
    }
    
    @IBAction func logoutBnAction(_ sender: UIButton) {
        self.performSegue(withIdentifier: SIGN_IN_PAGE_VIEW_SEGUE_IDENTIFIER, sender: sender)
    }
    
    @IBAction func mapButtonAction(_ sender: UIButton) {
        self.performSegue(withIdentifier: MAP_VIEW_SEGUE_IDENTIFIER, sender: sender)
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if (segue.identifier == SIGN_IN_PAGE_VIEW_SEGUE_IDENTIFIER) {
            deleteUserFromDefaults()
            self.dismiss(animated: true, completion: nil)
        }
    }
}
