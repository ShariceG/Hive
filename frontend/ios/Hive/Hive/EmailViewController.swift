//
//  EmailViewController.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 4/5/20.
//  Copyright © 2020 Chuck Onwuzuruike. All rights reserved.
//

import UIKit

class EmailViewController : UIViewController, SignInPageFragment {
    
    @IBOutlet weak var emailTextField: UITextField!
    public var signInDelegate: SignInDelegate?
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    func setSignInPageDelegate(delegate: SignInDelegate) {
        signInDelegate = delegate
    }
    
    @IBAction func nextBnAction(_ sender: UIButton) {
        let email = emailTextField.text!
        if (email.isEmpty) {
            return
        }
        print("Email: " + email)
        // Send email to server.
        signInDelegate?.goEnterPinCode()
    }
    
}
