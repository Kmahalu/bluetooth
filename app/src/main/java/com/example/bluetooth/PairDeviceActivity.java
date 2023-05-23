package com.example.bluetooth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PairDeviceActivity extends AppCompatActivity {
    private BluetoothAdapter bluetoothAdapter;
    private ListView deviceListView;
    private List<BluetoothDevice> discoverableDevices;
    private ArrayAdapter<String> deviceAdapter;
    private final BroadcastReceiver discoveryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            System.out.println("-------------------------");
            System.out.println(action);
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null && !discoverableDevices.contains(device)) {
                    discoverableDevices.add(device);
                    deviceAdapter.add(device.getName());
                    deviceAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pair_device);

        deviceListView = findViewById(R.id.deviceListView);

        // Get the default Bluetooth adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Check if Bluetooth is supported on the device
        if (bluetoothAdapter == null) {
            // Bluetooth is not supported
            // Handle the case accordingly
            return;
        }

        // Check if Bluetooth is enabled
        if (!bluetoothAdapter.isEnabled()) {
            // Bluetooth is not enabled
            // Handle the case accordingly
            return;
        }

        // Get the list of paired devices
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        List<String> deviceNames = new ArrayList<>();

        // Add the device names to the list
        for (BluetoothDevice device : pairedDevices) {
            deviceNames.add(device.getName());
        }

        // Create an adapter to display the device names in the ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, deviceNames);
        deviceListView.setAdapter(adapter);

        findViewById(R.id.pairNewDeviceButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discoverDevices();
            }
        });

        // Create the list adapter
        deviceAdapter = new ArrayAdapter<>(PairDeviceActivity.this, android.R.layout.simple_list_item_1);
    }

    private void discoverDevices() {
        // Register the BroadcastReceiver for device discovery
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(discoveryReceiver, filter);

        // Start device discovery
        bluetoothAdapter.startDiscovery();

        // Initialize the list of discoverable devices
        discoverableDevices = new ArrayList<>();

        // Create the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Discoverable Devices")
                .setAdapter(deviceAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Get the clicked device
                        BluetoothDevice clickedDevice = discoverableDevices.get(which);

                        // Perform actions with the clicked device
                        // Example: Connect to the device or display more information
                        Toast.makeText(PairDeviceActivity.this, "Clicked device: " + clickedDevice.getName(), Toast.LENGTH_SHORT).show();
                    }
                })
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Stop device discovery and unregister the BroadcastReceiver
                        bluetoothAdapter.cancelDiscovery();
                        unregisterReceiver(discoveryReceiver);
                    }
                });

        AlertDialog dialog = builder.create();

        // Inflate the custom layout for the dialog
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialogue_discoverable, null);

        // Set the custom layout to the dialog
        dialog.setView(dialogLayout);

        // Show the dialog
        dialog.show();
    }

}