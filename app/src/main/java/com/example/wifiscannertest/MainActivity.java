package com.example.wifiscannertest;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Element [] nets;
    private WifiManager wifiManager;
    private List<ScanResult> wifiList;
    private String ssid111;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //разрешение на использование данных местоположения в приложениии !!!ВАЖНО ЧТОБЫ GPS БЫЛ ВКЛЮЧЕН НА УСТРОЙСТВЕ
        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},1);
        //вызов метода включения wifi
        enableWifi();
        //вызов метода searchWiFi
        searchWiFi();
        //подключаемся к сети
       // myConnect();
    } //onCreate

//------------------------------------------------------------------------
    //МЕТОД ВКЛЮЧЕНИЕ МОДУЛЯ Wi-Fi
    public void enableWifi() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
            Toast.makeText(getApplicationContext(), "Wifi включен", Toast.LENGTH_SHORT).show();
        }
    }
//------------------------------------------------------------------------
    //МЕТОД searchWiFi() ПОЛУЧАЕТ СПИСОК WIFI СЕТЕЙ И ОТПРАВЛЯЕТ В АДАПТЕР
    @SuppressLint("WifiManagerLeak")
    private void searchWiFi(){
        Log.i("TAG","*******************************************************************");
        wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();
        wifiList = wifiManager.getScanResults(); // сохраняем результат сканирования в список
        for (int i = 0; i < wifiList.size(); i++) {
            String item = wifiList.get(i).toString();
            System.out.println(item);
        }

        //создаем массив элементов и сортируем данные
        nets = new Element[wifiList.size()];
        for (int i = 0; i<wifiList.size(); i++){
            String item = wifiList.get(i).toString();

            String[] vector_item = item.split(","); //получаем массив, элемент разделен запятой
            String item_essid = vector_item[0];
            String ssid = item_essid.split(":")[1];
            ssid111 = ssid;
            String item_capabilities = vector_item[2];
            String security = item_capabilities.split(":")[1];

            String item_level = vector_item[3];
            String level = item_level.split(":")[1];

            Log.i("TAG","ssid="+ssid + "security="+ security + "level="+ level);
            nets[i] = new Element(ssid, security, level);
        }
        //создаем адаптер
        AdapterElements adapterElements = new AdapterElements(this);
        //находим наш лист вью
        ListView netList = (ListView)findViewById(R.id.listViewWiFi);
        //присваиваем адаптер нашему лист вью
        netList.setAdapter(adapterElements);
        netList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MainActivity.this, "position="+i, Toast.LENGTH_SHORT).show();

            }
        });
    }//searchWiFi
//-----------------------------------------------------------------------

    //КНОПКА SEARCH WIFI
    public void onClickSearchWIFI(View view) {
        searchWiFi();
    }

    //АДАПТЕР ЗАПОЛНЕНИЯ ДАННЫХ
    public class AdapterElements extends ArrayAdapter<Object> {

        Activity context;

        public AdapterElements(Activity context) {
            super(context, R.layout.items, nets); // параметры (контекст, макет пункта списка, и массив элементов списка)
            this.context = context;
        }

        public View getView(int position, View convertView, ViewGroup parent){ //заполнение элемента списка данными
            LayoutInflater inflater = context.getLayoutInflater(); // с помощью инфлайтор заполняем данными элемент списка
            View item = inflater.inflate(R.layout.items, null); // находим элемент списка по id

            //находим по id каждое текстовое поле и заполняем данными
            TextView tvSsid = (TextView) item.findViewById(R.id.tvSSID);
            tvSsid.setText(nets[position].getTitle());

            TextView tvSecurity = (TextView) item.findViewById(R.id.tvSecurity);
            tvSecurity.setText(nets[position].getSecurity());

            TextView tvLevel = (TextView) item.findViewById(R.id.tvLevel);
            String level = nets[position].getLevel();
            tvLevel.setText(level);
            return item;
        }
    }//AdapterElements

}//MainActivity
