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
    private var signInDelegate: SignInDelegate?
    private let client: ServerClient = ServerClient()
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
    
    @IBAction func nextBnAction(_ sender: UIButton) {
        let email = emailTextField.text!
        if (email.isEmpty) {
            return
        }
        print("Email: " + email)
        client.verifyExistingUser(email: email, completion: verifyExistingUserCompletion, notes: ["email": email])
    }
    
    @IBAction func goBackBnAction(_ sender: UIButton) {
        self.signInDelegate!.goLogInOrSignUp()
    }
    
    private func verifyExistingUserCompletion(responseOr: StatusOr<Response>, notes: [String:Any]?) {
        let baseStr: String = "createNewUserCompletion => "
        if (responseOr.hasError()) {
            // Handle likley connection error
            print(baseStr + "Connection Failure: " + responseOr.getErrorMessage())
            return
        }
        let response = responseOr.get()
        if (!response.ok()) {
            // Handle server error
            print(baseStr + "ServerStatusCode: " + String(describing: response.serverStatusCode))
            return
        }
        if notes == nil {
            print(baseStr + "Expected notes!")
            return
        }
        let username = response.username
        let email = notes!["email"] as! String
        DispatchQueue.main.async {
            self.signInDelegate?.goEnterPinCode(
                args: ["username": username, "email": email])
        }
    }
}
