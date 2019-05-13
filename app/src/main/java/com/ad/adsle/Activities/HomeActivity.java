package com.ad.adsle.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.ad.adsle.Db.AppData;
import com.ad.adsle.Information.Plans;
import com.ad.adsle.Information.User;
import com.ad.adsle.R;
import com.ad.adsle.Util.Utils;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.bijoysingh.starter.util.PermissionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.provider.ContactsContract;
import android.text.InputType;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    AppData data;
    Utils utils;
    User user;

    TextView name, email, nBonus;
    NavigationView navigationView;

    static final int CONTACT_PICKER_REQUEST = 123;
    private static int PLANS_REQUEST = 143;
    private static int REQUEST_INVITE = 642;

    Plans plan = null;
    AppCompatTextView edit_plan;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        utils = new Utils(HomeActivity.this);
        data = new AppData(HomeActivity.this);
        user = data.getUser();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        nBonus = findViewById(R.id.tvBonus);
        edit_plan = findViewById(R.id.textplan);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, CreateCampaignActivity.class));
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        LoadNavHeaderDetails();
    }

    private void LoadNavHeaderDetails() {
        nBonus.setText(utils.getExactDataValue(user.getBonus_data()));
        name = navigationView.getHeaderView(0).findViewById(R.id.tvName);
        email = navigationView.getHeaderView(0).findViewById(R.id.tvEmail);
        name.setText(user.getName());
        email.setText(user.getEmail());
    }

    String choice = "", numberToRecharge = "";

    public void ChoosePlans(View view) {
        if (!dataCheck()) {
            utils.error("Sorry, you have to accumulate 500MB worth of data before you can recharge.");
            return;
        }
        int id = view.getId();
        if (id == R.id.rechargeM || id == R.id.planchoose) {
            choice = "self";
            numberToRecharge = user.getNumber();
            startActivityForResult(new Intent(HomeActivity.this, PlansActivity.class).putExtra("numberToRecharge", numberToRecharge), PLANS_REQUEST);
        }
        if (id == R.id.rechargeO) {
            choice = "others";
            RechargeOthersPopupDialog();
        }
    }

    public void RechargeNow(View view) {
        if (plan == null) {
            Toast.makeText(getApplicationContext(), "Please select a plan", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(HomeActivity.this, TopupActivity.class);
        intent.putExtra("plan", plan);
        intent.putExtra("number", numberToRecharge);
        intent.putExtra("choice", choice);
        startActivity(intent);
    }

    private void RechargeOthersPopupDialog() {
        new MaterialDialog.Builder(HomeActivity.this)
                .cancelable(false)
                .canceledOnTouchOutside(true)
                .title("Enter contact number")
                .input("08101234567", "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        numberToRecharge = String.valueOf(input);
                        startActivityForResult(new Intent(HomeActivity.this, PlansActivity.class).putExtra("numberToRecharge", numberToRecharge), PLANS_REQUEST);
                    }
                }).inputRange(11, 14, ContextCompat.getColor(this, R.color.colorPrimaryDark))
                .inputType(InputType.TYPE_CLASS_PHONE)
                .negativeText("Select Contact")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        PermissionManager pm = new PermissionManager(HomeActivity.this, new String[]{android.Manifest.permission.READ_CONTACTS});
                        if (pm.hasAllPermissions()) {
                            Intent i = new Intent(Intent.ACTION_PICK);
                            i.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                            startActivityForResult(i, CONTACT_PICKER_REQUEST);
                        } else {
                            pm.requestPermissions(663);
                        }
                    }
                }).show();
    }

    private boolean dataCheck() {
        long current_data = Long.parseLong(user.getBonus_data());
        long start_data = 524288000;
        return (current_data >= start_data);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PLANS_REQUEST) {
            plan = data.getParcelableExtra("resultPlan");
            edit_plan.setText(plan.getPrice() + " - " + plan.getTitle());
            Intent intent = new Intent(HomeActivity.this, TopupActivity.class);
            intent.putExtra("plan", plan);
            intent.putExtra("number", numberToRecharge);
            intent.putExtra("choice", choice);
            startActivity(intent);
        }
        if (requestCode == CONTACT_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri contactUri = data.getData();
                String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
                Cursor cursor = getContentResolver().query(contactUri, projection,
                        null, null, null);
                // If the cursor returned is valid, get the phone number
                if (cursor != null && cursor.moveToFirst()) {
                    int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    String number = cursor.getString(numberIndex);
                    numberToRecharge = number;
                    startActivityForResult(new Intent(HomeActivity.this, PlansActivity.class).putExtra("numberToRecharge", numberToRecharge), PLANS_REQUEST);
                }
                cursor.close();
            } else if (resultCode == RESULT_CANCELED) {
                System.out.println("User closed the picker without selecting items.");
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_manage) {
            // Handle the camera action
        } else if (id == R.id.nav_trans) {

        }
        if (id == R.id.nav_invite) {
            startActivity(new Intent(HomeActivity.this, InviteActivity.class));
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
        }
        if (id == R.id.nav_data_balance) {
            CheckDataBalance();
        } else if (id == R.id.nav_logout) {
            new MaterialDialog.Builder(HomeActivity.this)
                    .title("Confirmation")
                    .content("Log me out now!")
                    .negativeText("Cancel")
                    .positiveText("Continue")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            data.Logout();
                            finish();
                        }
                    })
                    .show();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void CheckDataBalance() {
        PermissionManager permissionManager = new PermissionManager(HomeActivity.this, new String[]{android.Manifest.permission.CALL_PHONE});
        if (!permissionManager.hasAllPermissions()) {
            permissionManager.requestPermissions(232);
        } else {
            dialDataBalanceCode();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 232) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dialDataBalanceCode();
            }
        }
        if (requestCode == 663) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(i, CONTACT_PICKER_REQUEST);
            }
        }
    }

    private void dialDataBalanceCode() {
        String code = "";
        if (utils.GetNetworkProviderType(user.getNumber()).contentEquals("glo")) {
            code = "%23124%23";
        }
        if (utils.GetNetworkProviderType(user.getNumber()).contentEquals("mtn")) {
            code = "*131*4%23";
        }
        if (utils.GetNetworkProviderType(user.getNumber()).contentEquals("airtel")) {
            code = "*123%23";
        }
        if (utils.GetNetworkProviderType(user.getNumber()).contentEquals("9mobile")) {
            code = "*232%23";
        }
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + code));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(intent);
    }
}
