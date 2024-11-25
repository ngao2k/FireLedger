package com.example.fireledger;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private BillingAdapter adapter;
    private List<BillingItem> billingList;
    private BillingDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.billingList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 初始化数据库
        databaseHelper = new BillingDatabaseHelper(this);

        // 从数据库加载数据
        billingList = databaseHelper.getAllBillings();
        adapter = new BillingAdapter(billingList);
        recyclerView.setAdapter(adapter);

        // 添加左滑删除功能
        addSwipeToDelete();

        findViewById(R.id.addButton).setOnClickListener(view -> showAddBillingDialog());
        findViewById(R.id.needReportText).setOnClickListener(view -> {
            // 跳转到报告页面
            startActivity(new Intent(MainActivity.this, LLMActivity.class));
        });
    }

    private void addSwipeToDelete() {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false; // 不支持拖动排序
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                BillingItem item = billingList.get(position);

                // 从数据库中删除
                databaseHelper.deleteBilling(item);

                // 从列表中删除
                billingList.remove(position);
                adapter.notifyItemRemoved(position);

                Toast.makeText(MainActivity.this, "Deleted: " + item.getType() + " - " + item.getAmount(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    Paint paint = new Paint();
                    paint.setColor(Color.RED);
                    float itemViewTop = viewHolder.itemView.getTop();
                    float itemViewBottom = viewHolder.itemView.getBottom();
                    float itemViewRight = viewHolder.itemView.getRight();
                    float itemViewLeft = itemViewRight + dX;

                    c.drawRect(itemViewLeft, itemViewTop, itemViewRight, itemViewBottom, paint);

                    // 添加删除图标
                    int iconMargin = (viewHolder.itemView.getHeight() - 64) / 2;
                    int iconLeft = (int) (itemViewRight - iconMargin - 64);
                    int iconTop = (int) (itemViewTop + iconMargin);
                    int iconRight = (int) (itemViewRight - iconMargin);
                    int iconBottom = (int) (itemViewBottom - iconMargin);

                    Drawable icon = ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_delete);
                    if (icon != null) {
                        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                        icon.draw(c);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void showAddBillingDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    LayoutInflater inflater = getLayoutInflater();
    View dialogView = inflater.inflate(R.layout.dialog_add_billing, null);
    builder.setView(dialogView);

    Button datePickerButton = dialogView.findViewById(R.id.datePickerButton);
    Spinner typeSpinner = dialogView.findViewById(R.id.typeSpinner);
    EditText amountInput = dialogView.findViewById(R.id.amountInput);
    EditText descriptionInput = dialogView.findViewById(R.id.descriptionText);
    Button saveButton = dialogView.findViewById(R.id.saveButton);

    final String[] selectedDate = {""};
    datePickerButton.setOnClickListener(view -> {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (datePicker, year, month, dayOfMonth) -> {
                    selectedDate[0] = year + "-" + (month + 1) + "-" + dayOfMonth;
                    datePickerButton.setText(selectedDate[0]);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    });

    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
            this, R.array.transaction_types, android.R.layout.simple_spinner_item);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    typeSpinner.setAdapter(adapter);

    AlertDialog dialog = builder.create();
    saveButton.setOnClickListener(view -> {
        String date = selectedDate[0];
        String type = typeSpinner.getSelectedItem().toString();
        String amount = amountInput.getText().toString();
        String description = descriptionInput.getText().toString();

        if (date.isEmpty() || type.isEmpty() || amount.isEmpty()) {
            Toast.makeText(this, "请填写所有字段", Toast.LENGTH_SHORT).show();
        } else {
            // 添加账单到数据库
            databaseHelper.addBilling(date, type, amount, description);

            // 创建新的账单对象
            BillingItem newItem = new BillingItem(date, type, amount, description);

            // 更新账单列表
            billingList.add(0, newItem); // 添加到列表顶部
            adapter.getItemId(0); // 通知 RecyclerView 插入数据
            recyclerView.scrollToPosition(0); // 滚动到顶部

            dialog.dismiss();
            Toast.makeText(this, "账单已添加", Toast.LENGTH_SHORT).show();
        }
    });

    dialog.show();
}

}
