//
//  EmailViewController.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 4/5/20.
//  Copyright © 2020 Chuck Onwuzuruike. All rights reserved.
//

import UIKit

class EnterPinCodeViewController : UIViewController, SignInPageFragment {
    
    @IBOutlet weak var pinCodeTextField: UITextField!
    public var signInDelegate: SignInDelegate?
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    func setSignInPageDelegate(delegate: SignInDelegate) {
        signInDelegate = delegate
    }

    @IBAction func hiveInBnAction(_ sender: UIButton) {
        let pin = pinCodeTextField.text!
        if (pin.isEmpty) {
            return
        }
        print("Pin: " + pin)
        // Verify pin.
        
        signInDelegate?.goToMainAppOrWelcomeIfLogIn()
    }
}
