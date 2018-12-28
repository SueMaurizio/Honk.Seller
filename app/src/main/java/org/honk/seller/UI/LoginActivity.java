package org.honk.seller.UI;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.honk.seller.PreferencesHelper;
import org.honk.seller.R;
import org.honk.seller.model.AuthenticationType;
import org.honk.seller.model.User;
import org.json.JSONException;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private CallbackManager facebookCallbackManager;
    private GoogleSignInClient googleSignInClient;

    private static final int REQUEST_CODE_GOOGLE_SIGN_IN = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This is required for the Facebook authentication to work.
        setContentView(R.layout.activity_login);

        if(!this.tryGoogleLogin()) {
            // The user is not authenticated with Google: try Facebook authentication.
            if (!this.tryFacebookLogin()) {
                // The user is not authenticated with Facebook as well: set up the login buttons.
                this.setupFacebookAuthentication();
                this.setupGoogleButton();
            } else {
                // The user is authenticated with Facebook: move on to the next activity.
                this.goToCompanyDetails();
            }
        } else {
            // The user is authenticated with Google: move on to the next activity.
            this.goToCompanyDetails();
        }
    }

    private void goToCompanyDetails() {
        Intent companyDetailsActivityIntent = new Intent(LoginActivity.this, CompanyDetailsActivity.class);
        startActivity(companyDetailsActivityIntent);
    }

    private boolean tryFacebookLogin() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null && !accessToken.isExpired()) {
            Profile profile = Profile.getCurrentProfile();
            this.setCurrentUser(new User(profile.getId(), AuthenticationType.facebook, profile.getFirstName()));
            return true;
        }

        return false;
    }

    private void setupFacebookAuthentication () {

        facebookCallbackManager = CallbackManager.Factory.create();

        LoginButton facebookLoginButton = findViewById(R.id.facebookLoginButton);
        facebookLoginButton.setReadPermissions("public_profile");

        facebookLoginButton.registerCallback(facebookCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        setFacebookUser(loginResult.getAccessToken());
                        goToCompanyDetails();
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.e(LoginActivity.class.getCanonicalName(), error.getMessage());
                    }
                }
        );
    }

    private void setFacebookUser(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                (object, response) -> {
                        try {
                            setCurrentUser(new User(object.getString("id"), AuthenticationType.facebook, object.getString("name")));
                            this.goToCompanyDetails();
                        } catch (JSONException x) {
                            Log.e(LoginActivity.class.getCanonicalName(), x.getMessage());
                        }
                });

        request.executeAsync();
    }

    private boolean tryGoogleLogin() {

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();

        // Build a GoogleSignInClient with the options specified by gso.
        this.googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            setCurrentUser(new User(account.getId(), AuthenticationType.google, account.getDisplayName()));
            return true;
        } else {
            return false;
        }
    }

    private void setupGoogleButton() {
        this.findViewById(R.id.sign_in_button).setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, REQUEST_CODE_GOOGLE_SIGN_IN);
                break;
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            setCurrentUser(new User(account.getId(), AuthenticationType.google, account.getDisplayName()));

            // Signed in successfully, move to the next step.
            this.goToCompanyDetails();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.

            new AlertDialog.Builder(this)
                    .setMessage(this.getString(R.string.loginFailed))
                    .setTitle(R.string.retry)
                    .show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == REQUEST_CODE_GOOGLE_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        } else {
            this.facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void setCurrentUser(User user) {
        // Set the main properties of the current user. For privacy reasons, personal data is not saved to any database.
        PreferencesHelper.setUser(user, this);
    }
}