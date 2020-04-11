//
//  EmailViewController.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 4/5/20.
//  Copyright © 2020 Chuck Onwuzuruike. All rights reserved.
//

import UIKit

class EmailViewController : UIViewController, SignInPageFragment {
    
    @IBOutlet weak var unverifiedUserLabel: UILabel!
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
        let discovered = args["discoveredUnverifiedUser"]
        if discovered == nil || !(discovered as! Bool) {
            return
        }
        let user = args["user"] as! User
        unverifiedUserLabel.text = "Hey " + user.username +
            ", please verify your email. Not " + user.username + "? No biggie, tap Go Back!"
    }
    
    @IBAction func nextBnAction(_ sender: UIButton) {
        let email = emailTextField.text!
        if (email.isEmpty) {
            showAlert(message: "Email cannot be empty")
            return
        }
        disableUserActivity()
        client.verifyExistingUser(email: email, completion: verifyExistingUserCompletion, notes: ["email": email])
    }
    
    @IBAction func goBackBnAction(_ sender: UIButton) {
        self.signInDelegate!.goLogInOrSignUp()
    }
    
    private func verifyExistingUserCompletion(responseOr: StatusOr<Response>, notes: [String:Any]?) {
        if (responseOr.hasError()) {
            showInternalServerErrorAlert()
            return
        }
        let response = responseOr.get()
        if (!response.ok()) {
            showAlert(title: "Oh...", message: response.serverMessage)
            return
        }
        let username = response.username
        let email = notes!["email"] as! String
        signInDelegate!.saveLogInData(username: username, email: email, isSignUpVerified: false)
        DispatchQueue.main.async {
            self.signInDelegate?.goEnterPinCode(
                args: ["username": username, "email": email])
        }
    }
}
