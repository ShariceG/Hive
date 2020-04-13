//
//  LogInViewController.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 4/4/20.
//  Copyright © 2020 Chuck Onwuzuruike. All rights reserved.
//

import UIKit

class LogInViewController : UIViewController, SignInPageFragment {
    
    private var signInDelegate: SignInDelegate?
    private var args: [String:Any]?
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    func setSignInPageDelegate(delegate: SignInDelegate) {
        signInDelegate = delegate
    }
    
    func setArgs(args: [String:Any]) {
        self.args = args
    }
    
    @IBAction func logInBnAction(_ sender: UIButton) {
        signInDelegate?.goEnterEmailAddress(args: [String:Any]())
    }
    
    @IBAction func signUpBnAction(_ sender: UIButton) {
        signInDelegate?.goEnterEmailAddressAndUsername(args: [String: String]())
    }
}
