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
        let username = usernameTextField.text!
        let email = emailTextField.text!
        if (username.isEmpty || email.isEmpty) {
            return
        }
        self.view.isUserInteractionEnabled = false
        print("Username: " + username)
        print("Email: " + email)
        client.createNewUser(username: username, email: email, completion: createNewUserCompletion, notes: ["username": username, "email": email])
    }
    
    @IBAction func goBackBnAction(_ sender: UIButton) {
        self.signInDelegate!.goLogInOrSignUp()
    }
    
    private func createNewUserCompletion(responseOr: StatusOr<Response>, notes: [String:Any]?) {
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
        let username = notes!["username"] as! String
        let email = notes!["email"] as! String
        DispatchQueue.main.async {
            self.signInDelegate?.goEnterPinCode(
                args: ["username": username, "email": email])
        }
    }
    
}
