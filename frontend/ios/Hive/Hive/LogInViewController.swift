//
//  LogInViewController.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 4/4/20.
//  Copyright © 2020 Chuck Onwuzuruike. All rights reserved.
//

import UIKit

class LogInViewController : UIViewController, SignInPageFragment {
    
    public var signInDelegate: SignInDelegate?
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    func setSignInPageDelegate(delegate: SignInDelegate) {
        signInDelegate = delegate
    }
    
    @IBAction func logInBnAction(_ sender: UIButton) {
        signInDelegate?.goEnterEmailAddress()
    }
    
    @IBAction func signUpBnAction(_ sender: UIButton) {
        signInDelegate?.goEnterEmailAddressAndUsername()
    }
}
