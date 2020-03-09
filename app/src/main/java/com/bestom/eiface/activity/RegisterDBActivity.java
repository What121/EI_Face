package com.bestom.eiface.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bestom.ei_library.EIFace;
import com.bestom.ei_library.commons.beans.UserInfoDBBean;
import com.bestom.eiface.Handler.RegisterDBHandler;
import com.bestom.eiface.MyApp;
import com.bestom.eiface.R;
import com.bestom.eiface.view.adapter.RegisterDB_UserInforAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RegisterDBActivity extends BaseActivity {
    private static final String TAG = "RegisterDBActivity";
    private final int INIT_DATA=111;
    private Context mContext;
    private Activity mActivity;
    private Unbinder mUnbinder;

    RegisterDBHandler mRegisterDBHandler;
    RegisterDB_UserInforAdapter mRegisterDBUserInforAdapter;
    RecyclerView recyclerView;
    String assetPath;
    List<UserInfoDBBean> mUserInfoDBBeanList;
    LinearLayout enrolled_linear;
    SearchView searchView;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_db);

        mContext=this;
        mActivity=this;
        mUnbinder= ButterKnife.bind(this);
        mRegisterDBHandler=new RegisterDBHandler(this);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Search Person");
        myToolbar.setTitleTextColor(Color.WHITE);
        myToolbar.setTitleMarginStart(210);
        setSupportActionBar(myToolbar);
        myToolbar.setNavigationIcon(R.drawable.back_button);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!searchView.isIconified()) {
                    searchView.setIconified(true);
                    return;
                }
                finish();
            }
        });

        sharedPreferences = getSharedPreferences("attendance_prefrence", Context.MODE_PRIVATE);
        enrolled_linear = (LinearLayout) findViewById(R.id.enrolled_dabatase_list_linear);
        assetPath = MyApp.DualFilePath+File.separator;
        recyclerView = (RecyclerView) findViewById(R.id.enrolled_user_listview);
        mUserInfoDBBeanList = new ArrayList<>();

        initDatabaseList();

        boolean isDebug = sharedPreferences.getBoolean("debug_switch",false);

        mRegisterDBUserInforAdapter = new RegisterDB_UserInforAdapter(mContext, mUserInfoDBBeanList,
                false,true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mRegisterDBUserInforAdapter);
        mRegisterDBUserInforAdapter.notifyDataSetChanged();

        //region SwipeHelper
//        SwipeHelper swipeHelper = new SwipeHelper(this, recyclerView) {
//            @Override
//            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
//                underlayButtons.add(new SwipeHelper.UnderlayButton(
//                        "Delete",
//                        0,
//                        Color.parseColor("#1CB5E0"),
//                        new SwipeHelper.UnderlayButtonClickListener() {
//                            @Override
//                            public void onClick(int pos) {
//                                // TODO: onDelete
//                                showDeleteDialog(pos,mRegisterDBUserInforAdapter.getFilteredenrolledDatabaseObjectList().
//                                        get(pos).getImage_id());
//                            }
//                        }
//                ));
//            }
//        };

//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeHelper);
//        itemTouchHelper.attachToRecyclerView(recyclerView);
        //endregion

    }

    private void showDeleteDialog(final int position,final int recordID){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("Delete Record!!");

        // Setting Dialog Message
        alertDialog.setMessage("Are you sure you want Delete this Record?");

        // Setting Icon to Dialog

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                // Write your code here to invoke YES event

//                wffrdualcamapp.deletePerson(recordID);
                EIFace.deletePerson(recordID);
                Toast.makeText(RegisterDBActivity.this, "Record Deleted", Toast.LENGTH_SHORT).show();
                mRegisterDBUserInforAdapter.removeItem(position);
            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    private void initDatabaseList() {
//        Log.i(TAG,"INIT : "+ wffrdualcamapp.getDatabase());
//        String[] userinfos = (String[]) wffrdualcamapp.getDatabaseNames();
//        int[] recordIDs = wffrdualcamapp.getDatabaseRecords();
        Log.i(TAG,"INIT : "+ EIFace.getEIDatabase());
        String[] IDs = EIFace.getEIDatabaseIDs();
        int[] recordIDs = EIFace.getEIDatabaseRecords();

        if (IDs!=null){
            for (int i = 0; i < IDs.length; i++) {
                String ID = IDs[i];
                String name = EIFace.getDBNamebyID(ID);
                mUserInfoDBBeanList.add(new UserInfoDBBean(getPath(assetPath +
                        "wffrdb/pid" + recordIDs[i]),name,ID , recordIDs[i]));
            }
        }

        //sort
        Collections.sort(mUserInfoDBBeanList,new Comparator<UserInfoDBBean>() {
            @Override
            public int compare(UserInfoDBBean t0, UserInfoDBBean t1) {
                return t0.getName().compareToIgnoreCase(t1.getName());
            }
        });
    }

    public void supplementInitDB(){
        initDatabaseList();
        mRegisterDBUserInforAdapter = new RegisterDB_UserInforAdapter(mContext, mUserInfoDBBeanList, false,true);
        mRegisterDBUserInforAdapter.notifyDataSetChanged();
    }


    public static String getPath(String path) {
        File directory = new File(path);
        File[] files = directory.listFiles();
        if (files.length > 0) {
            return files[files.length - 1].getAbsolutePath();
        } else {
            return files[0].getAbsolutePath();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.enrolled_database_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mRegisterDBUserInforAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mRegisterDBUserInforAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Cursor cursor = EIFace.getDBALL();
        Log.d(TAG, "cursor count: "+cursor.getCount());
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Log.d(TAG, "cursor  recordid"+cursor.getInt(0) +"  ID:"+cursor.getString(1)+"   name:"+cursor.getString(2));
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }
}
