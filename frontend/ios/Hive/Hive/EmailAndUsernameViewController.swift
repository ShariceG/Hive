//
//  EmailViewController.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 4/5/20.
//  Copyright © 2020 Chuck Onwuzuruike. All rights reserved.
//

import UIKit

class EmailAndUsernameViewController : UIViewController, SignInPageFragment {
    
    @IBOutlet weak var usernameTextField: UITextField!
    @IBOutlet weak var emailTextField: UITextField!
    
    public var signInDelegate: SignInDelegate?
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    func setSignInPageDelegate(delegate: SignInDelegate) {
        signInDelegate = delegate
    }

    @IBAction func nextBnAction(_ sender: UIButton) {
        let username = usernameTextField.text!
        let email = emailTextField.text!
        if (username.isEmpty || email.isEmpty) {
            return
        }
        print("Username: " + username)
        print("Email: " + email)
        // Send username and email to server.
        signInDelegate?.goEnterPinCode()
    }
    
}
