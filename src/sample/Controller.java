package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import org.apache.commons.io.IOUtils;


public class Controller implements Initializable {


    @FXML    private Rectangle iconSenate, iconTimber, iconFarm, iconQuarry, iconWarehouse, iconSilverMine, iconBarracks, iconTemple, iconMarket, iconHarbor, iconAcademy, iconCityWall, iconCave, iconTheatre, iconThermalBaths, iconLibrary, iconLighthouse, iconTower, iconDivineStatue, iconOracle, iconMerchants, iconPlow, iconBunks;
    @FXML    private Rectangle iconZeus, iconAthena, iconPoseidon, iconHera, iconHades, iconArtemis, iconAphrodite, iconAres;
    @FXML    private Rectangle iconSwordsman, iconSlinger, iconArcher, iconHoplite, iconHorseman, iconChariot, iconCatapult, iconBireme, iconLightShip, iconFireShip, iconTrireme, iconColony, iconSlowBoat, iconFastBoat;
    @FXML    private Rectangle iconDivineEnvoy, iconMyth1, iconMyth2;
    @FXML    private Spinner<Integer> spnSenate, spnTimber, spnFarm, spnQuarry, spnWarehouse, spnSilverMine, spnBarracks, spnTemple, spnMarket, spnHarbor, spnAcademy, spnCityWall, spnCave, spnBonusPoblation;
    @FXML    private TextField tfMaxPoblation, tfAvaiablePoblation;
    @FXML    private CheckBox ckbTheatre, ckbThermalBaths, ckbLibrary, ckbLighthouse, ckbTower, ckbDivineStatue, ckbOracle, ckbMerchants, ckbPlow, ckbBunks;
    @FXML    private TextField maxSwordsman, maxSlinger, maxArcher, maxHoplite, maxHorseman, maxChariot, maxCatapult, maxBireme, maxLightShip, maxTrireme, maxFireShip, maxColony, maxSlowBoat, maxFastBoat, maxDivineEnvoy, maxMyth1, maxMyth2;
    @FXML    private TextField tfSwordsman, tfSlinger, tfArcher, tfHoplite, tfHorseman, tfChariot, tfCatapult, tfBireme, tfLightShip, tfTrireme, tfFireShip, tfColony, tfSlowBoat, tfFastBoat, tfDivineEnvoy, tfMyth1, tfMyth2;

    private JSONObject unitsJSON, buildingsJSON;
    private HashMap<Spinner<Integer>, String> hashMapBuildings = new HashMap<>();
    private HashMap<HashMap<TextField, String>, String> hashMapUnits = new HashMap<>();

    private int maxPoblation = 0, usedPoblation = 1, bonusThermes = 0, bonusAphrodite = 0, avaiablePoblation, unitsPoblation = 0;
    private boolean usingThermes = false, usingAphrodite = false, usingPlow = false, usingBunks = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        setIcons();
        preloadData();

    }

    private void preloadData() {

        // #CITY_BUILDER

        try {
            InputStream isBuilings = new FileInputStream("res/buildings.json");
            buildingsJSON = new JSONObject(IOUtils.toString(isBuilings));

            hashMapBuildings = new HashMap<>();
            hashMapBuildings.put(spnSenate, "senate");
            hashMapBuildings.put(spnTimber, "timber");
            hashMapBuildings.put(spnFarm, "farm");
            hashMapBuildings.put(spnQuarry, "quarry");
            hashMapBuildings.put(spnWarehouse, "warehouse");
            hashMapBuildings.put(spnSilverMine, "silver_mine");
            hashMapBuildings.put(spnBarracks, "barracks");
            hashMapBuildings.put(spnTemple, "temple");
            hashMapBuildings.put(spnMarket, "market_place");
            hashMapBuildings.put(spnHarbor, "harbour");
            hashMapBuildings.put(spnAcademy, "academy");
            hashMapBuildings.put(spnCityWall, "city_wall");
            hashMapBuildings.put(spnCave, "cave");

            for (Spinner<Integer> spn : hashMapBuildings.keySet()) {
                JSONArray levelsJSON = buildingsJSON.getJSONArray(hashMapBuildings.get(spn));
                spn.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, levelsJSON.length()));
                spn.valueProperty().addListener((observableValue, oldValue, newValue) -> {
                    if (oldValue > newValue)
                        calculatePoblationBuildings(spn.getValue()-1, levelsJSON.getInt(spn.getValue()), hashMapBuildings.get(spn), false);
                    else
                        calculatePoblationBuildings(spn.getValue()-1, levelsJSON.getInt(spn.getValue()-1), hashMapBuildings.get(spn), true);
                });
            }

            HashMap<CheckBox, String> hashMapSpecialBuildings1 = new HashMap<>();
            hashMapSpecialBuildings1.put(ckbTheatre, "theatre");
            hashMapSpecialBuildings1.put(ckbThermalBaths, "thermal_baths");
            hashMapSpecialBuildings1.put(ckbLibrary, "library");
            hashMapSpecialBuildings1.put(ckbLighthouse, "lighthouse");

            for(CheckBox ch: hashMapSpecialBuildings1.keySet()) {
                ch.setOnMouseClicked(event -> {
                    if (ch.selectedProperty().getValue()){
                        for (CheckBox ckb : hashMapSpecialBuildings1.keySet()) {
                            if (ckb.selectedProperty().getValue() && ckb != ch){
                                calculatePoblationSpecialBuildings(hashMapSpecialBuildings1.get(ckb), false);
                                System.out.println("Habia una seleccionada -60");
                            }
                            ckb.selectedProperty().setValue(false);
                        }
                        ch.selectedProperty().setValue(true);
                        calculatePoblationSpecialBuildings(hashMapSpecialBuildings1.get(ch), true);
                        System.out.println("Seleccionada! +60");

                    }else{
                        calculatePoblationSpecialBuildings(hashMapSpecialBuildings1.get(ch), false);
                        System.out.println("Deseleccionada... -60");

                    }
                });
            }

            HashMap<CheckBox, String> hashMapSpecialBuildings2 = new HashMap<>();
            hashMapSpecialBuildings2.put(ckbTower, "tower");
            hashMapSpecialBuildings2.put(ckbDivineStatue, "divine_statue");
            hashMapSpecialBuildings2.put(ckbOracle, "oracle");
            hashMapSpecialBuildings2.put(ckbMerchants, "merchants_shop");

            for(CheckBox ch: hashMapSpecialBuildings2.keySet()) {
                ch.setOnMouseClicked(event -> {
                    if (ch.selectedProperty().getValue()){
                        for (CheckBox ckb : hashMapSpecialBuildings2.keySet()) {
                            if (ckb.selectedProperty().getValue() && ckb != ch){
                                calculatePoblationSpecialBuildings(hashMapSpecialBuildings2.get(ckb), false);
                                System.out.println("Habia una seleccionada -60");
                            }
                            ckb.selectedProperty().setValue(false);
                        }
                        ch.selectedProperty().setValue(true);
                        calculatePoblationSpecialBuildings(hashMapSpecialBuildings2.get(ch), true);
                        System.out.println("Seleccionada! +60");

                    }else{
                        calculatePoblationSpecialBuildings(hashMapSpecialBuildings2.get(ch), false);
                        System.out.println("Deseleccionada... -60");

                    }
                });
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        ObservableList<Integer> arrayBonusPoblation = FXCollections.observableArrayList(0, 50, 100, 150, 200, 250, 300);
        spnBonusPoblation.setValueFactory(new SpinnerValueFactory.ListSpinnerValueFactory<Integer>(arrayBonusPoblation));
        spnBonusPoblation.setOnMouseClicked(event -> showPoblation());

        // TECNOLOGIES

        ckbPlow.setOnMouseClicked(event -> {
            if (ckbPlow.selectedProperty().getValue()){
                System.out.println("[DEBUG] - Arado activado!");
                usingPlow = true;
                showPoblation();
            }else{
                System.out.println("[DEBUG] - Arado desactivado!");
                usingPlow = false;
                showPoblation();
            }
        });

        ckbBunks.setOnMouseClicked(event -> {
            if (ckbBunks.selectedProperty().getValue()){
                System.out.println("[DEBUG] - Literas activadas!");
                usingBunks = true;
            }else{
                System.out.println("[DEBUG] - Literas desactivadas!");
                usingBunks = false;
            }
        });

        // UNITS

        try {
            InputStream isUnits = new FileInputStream("res/units.json");
            unitsJSON = new JSONObject(IOUtils.toString(isUnits));

            HashMap<TextField, String> hashMapLand = new HashMap<>();
            hashMapLand.put(tfSwordsman, "swordsmen");
            hashMapLand.put(tfSlinger, "slinger");
            hashMapLand.put(tfArcher, "archer");
            hashMapLand.put(tfHoplite, "hoplite");
            hashMapLand.put(tfHorseman, "horseman");
            hashMapLand.put(tfChariot, "chariot");
            hashMapLand.put(tfCatapult, "catapult");

            HashMap<TextField, String> hashMapSea = new HashMap<>();
            hashMapSea.put(tfBireme, "bireme");
            hashMapSea.put(tfTrireme, "trireme");
            hashMapSea.put(tfLightShip, "light_ship");
            hashMapSea.put(tfFireShip, "fire_ship");
            hashMapSea.put(tfColony, "colony_ship");
            hashMapSea.put(tfFastBoat, "fast_transport_boat");
            hashMapSea.put(tfSlowBoat, "transport_boat");

            hashMapUnits.put(hashMapLand, "land");
            hashMapUnits.put(hashMapSea, "sea");

            for (HashMap<TextField, String> hashMap : hashMapUnits.keySet()) {
                for (TextField tf : hashMap.keySet()) {
                    tf.textProperty().addListener((observable, oldValue, newValue) -> {
                        if (!(tf.getText().isBlank())){
                            if (!newValue.matches("\\d*")) {
                                tf.setText(newValue.replaceAll("[^\\d]", ""));
                            }
                            if (avaiablePoblation / unitsJSON.getJSONObject(hashMapUnits.get(hashMap)).getJSONObject(hashMap.get(tf)).getInt("poblation") < Integer.parseInt(tf.getText()))
                                tf.setText(Integer.toString(avaiablePoblation / unitsJSON.getJSONObject(hashMapUnits.get(hashMap)).getJSONObject(hashMap.get(tf)).getInt("poblation")));

                            calculatePoblationUnits();

                        }
                    });
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void calculatePoblationUnits() {

        unitsPoblation = 0;

        for (HashMap<TextField, String> hashMap : hashMapUnits.keySet()){
            for (TextField tf : hashMap.keySet()) {
                if (!(tf.getText().isBlank())){
                    unitsPoblation += (Integer.parseInt(tf.getText()) * unitsJSON.getJSONObject(hashMapUnits.get(hashMap)).getJSONObject(hashMap.get(tf)).getInt("poblation"));
                }
            }
        }

        System.out.println("UnitsPoblation = " + unitsPoblation);
        showUnits();

    }

    private void showPoblation(){

        tfMaxPoblation.setText(Integer.toString(maxPoblation));

        if (usingThermes){
            bonusThermes = maxPoblation / 10;
            tfMaxPoblation.setText(Integer.toString(Integer.parseInt(tfMaxPoblation.getText()) + bonusThermes));
        }

        if (usingAphrodite){
            bonusAphrodite = spnFarm.getValue() * 5;
            tfMaxPoblation.setText(Integer.toString(Integer.parseInt(tfMaxPoblation.getText()) + bonusAphrodite));
        }

        if (usingPlow){
            tfMaxPoblation.setText(Integer.toString(Integer.parseInt(tfMaxPoblation.getText()) + 200));
        }

        int poblation = Integer.parseInt(tfMaxPoblation.getText()) + spnBonusPoblation.getValue();
        tfMaxPoblation.setText(Integer.toString(poblation));

        tfAvaiablePoblation.setText(Integer.toString(Integer.parseInt(tfMaxPoblation.getText()) - usedPoblation));

        showUnits();

    }

    private void showUnits(){

        tfAvaiablePoblation.setText(Integer.toString(Integer.parseInt(tfAvaiablePoblation.getText()) - unitsPoblation));
        avaiablePoblation = Integer.parseInt(tfAvaiablePoblation.getText());

        maxSwordsman.setText(Integer.toString((avaiablePoblation / unitsJSON.getJSONObject("land").getJSONObject("swordsmen").getInt("poblation")) + (tfSwordsman.getText().isBlank() ? 0 : Integer.parseInt(tfSwordsman.getText()))));
        maxSlinger.setText(Integer.toString((avaiablePoblation / unitsJSON.getJSONObject("land").getJSONObject("slinger").getInt("poblation")) + (tfSlinger.getText().isBlank() ? 0 : Integer.parseInt(tfSlinger.getText()))));
        maxArcher.setText(Integer.toString((avaiablePoblation / unitsJSON.getJSONObject("land").getJSONObject("archer").getInt("poblation")) + (tfArcher.getText().isBlank() ? 0 : Integer.parseInt(tfArcher.getText()))));
        maxHoplite.setText(Integer.toString((avaiablePoblation / unitsJSON.getJSONObject("land").getJSONObject("hoplite").getInt("poblation")) + (tfHoplite.getText().isBlank() ? 0 : Integer.parseInt(tfHoplite.getText()))));
        maxHorseman.setText(Integer.toString((avaiablePoblation / unitsJSON.getJSONObject("land").getJSONObject("horseman").getInt("poblation")) + (tfHorseman.getText().isBlank() ? 0 : Integer.parseInt(tfHorseman.getText()))));
        maxChariot.setText(Integer.toString((avaiablePoblation / unitsJSON.getJSONObject("land").getJSONObject("chariot").getInt("poblation")) + (tfChariot.getText().isBlank() ? 0 : Integer.parseInt(tfChariot.getText()))));
        maxCatapult.setText(Integer.toString((avaiablePoblation / unitsJSON.getJSONObject("land").getJSONObject("catapult").getInt("poblation")) + (tfCatapult.getText().isBlank() ? 0 : Integer.parseInt(tfCatapult.getText()))));

        maxBireme.setText(Integer.toString((avaiablePoblation / unitsJSON.getJSONObject("sea").getJSONObject("bireme").getInt("poblation")) + (tfBireme.getText().isBlank() ? 0 : Integer.parseInt(tfBireme.getText()))));
        maxTrireme.setText(Integer.toString((avaiablePoblation / unitsJSON.getJSONObject("sea").getJSONObject("trireme").getInt("poblation")) + (tfTrireme.getText().isBlank() ? 0 : Integer.parseInt(tfTrireme.getText()))));
        maxLightShip.setText(Integer.toString((avaiablePoblation / unitsJSON.getJSONObject("sea").getJSONObject("light_ship").getInt("poblation")) + (tfLightShip.getText().isBlank() ? 0 : Integer.parseInt(tfLightShip.getText()))));
        maxFireShip.setText(Integer.toString((avaiablePoblation / unitsJSON.getJSONObject("sea").getJSONObject("fire_ship").getInt("poblation")) + (tfFireShip.getText().isBlank() ? 0 : Integer.parseInt(tfFireShip.getText()))));
        maxColony.setText(Integer.toString((avaiablePoblation / unitsJSON.getJSONObject("sea").getJSONObject("colony_ship").getInt("poblation")) + (tfColony.getText().isBlank() ? 0 : Integer.parseInt(tfColony.getText()))));
        maxFastBoat.setText(Integer.toString((avaiablePoblation / unitsJSON.getJSONObject("sea").getJSONObject("fast_transport_boat").getInt("poblation")) + (tfFastBoat.getText().isBlank() ? 0 : Integer.parseInt(tfFastBoat.getText()))));
        maxSlowBoat.setText(Integer.toString((avaiablePoblation / unitsJSON.getJSONObject("sea").getJSONObject("transport_boat").getInt("poblation")) + (tfSlowBoat.getText().isBlank() ? 0 : Integer.parseInt(tfSlowBoat.getText()))));

    }

    private void calculatePoblationBuildings(int index, int poblation, String s, boolean increment) {
        if (increment){
            if (s.equals("farm")) {
                maxPoblation += poblation;
            }else{
                usedPoblation -= poblation;
            }
        }else{
            if (s.equals("farm")) {
                maxPoblation -= poblation;
            }else{
                usedPoblation += poblation;
            }
        }

        showPoblation();

    }

    private void calculatePoblationSpecialBuildings(String s, boolean increment){
        if (increment){
            if(s.equals("thermal_baths")) {
                usingThermes = true;
                usedPoblation += 60;
            }else{
                usedPoblation += 60;
            }
        }else{
            if(s.equals("thermal_baths")) {
                System.out.println("Deseleccionada la terma");
                usingThermes = false;
                usedPoblation -= 60;
            }else{
                usedPoblation -= 60;
            }
        }

        showPoblation();

    }

    private void setIcons() {

        iconSenate.setFill(new ImagePattern(new Image(new File("res/images/buildings/senado.png").toURI().toString(), iconSenate.getWidth(), iconSenate.getHeight(), true, false)));
        iconTimber.setFill(new ImagePattern(new Image(new File("res/images/buildings/aserradero.png").toURI().toString(), iconSenate.getWidth(), iconSenate.getHeight(), true, false)));
        iconFarm.setFill(new ImagePattern(new Image(new File("res/images/buildings/granja.png").toURI().toString(), iconSenate.getWidth(), iconSenate.getHeight(), true, false)));
        iconQuarry.setFill(new ImagePattern(new Image(new File("res/images/buildings/cantera.png").toURI().toString(), iconSenate.getWidth(), iconSenate.getHeight(), true, false)));
        iconWarehouse.setFill(new ImagePattern(new Image(new File("res/images/buildings/almacen.png").toURI().toString(), iconSenate.getWidth(), iconSenate.getHeight(), true, false)));
        iconSilverMine.setFill(new ImagePattern(new Image(new File("res/images/buildings/mina.png").toURI().toString(), iconSenate.getWidth(), iconSenate.getHeight(), true, false)));
        iconBarracks.setFill(new ImagePattern(new Image(new File("res/images/buildings/cuartel.png").toURI().toString(), iconSenate.getWidth(), iconSenate.getHeight(), true, false)));
        iconTemple.setFill(new ImagePattern(new Image(new File("res/images/buildings/templo.png").toURI().toString(), iconSenate.getWidth(), iconSenate.getHeight(), true, false)));
        iconMarket.setFill(new ImagePattern(new Image(new File("res/images/buildings/mercado.png").toURI().toString(), iconSenate.getWidth(), iconSenate.getHeight(), true, false)));
        iconHarbor.setFill(new ImagePattern(new Image(new File("res/images/buildings/puerto.png").toURI().toString(), iconSenate.getWidth(), iconSenate.getHeight(), true, false)));
        iconAcademy.setFill(new ImagePattern(new Image(new File("res/images/buildings/academia.png").toURI().toString(), iconSenate.getWidth(), iconSenate.getHeight(), true, false)));
        iconCityWall.setFill(new ImagePattern(new Image(new File("res/images/buildings/muralla.png").toURI().toString(), iconSenate.getWidth(), iconSenate.getHeight(), true, false)));
        iconCave.setFill(new ImagePattern(new Image(new File("res/images/buildings/cueva.png").toURI().toString(), iconSenate.getWidth(), iconSenate.getHeight(), true, false)));
        iconTheatre.setFill(new ImagePattern(new Image(new File("res/images/buildings/teatro.png").toURI().toString(), iconSenate.getWidth(), iconSenate.getHeight(), true, false)));
        iconThermalBaths.setFill(new ImagePattern(new Image(new File("res/images/buildings/termas.png").toURI().toString(), iconSenate.getWidth(), iconSenate.getHeight(), true, false)));
        iconTower.setFill(new ImagePattern(new Image(new File("res/images/buildings/torre.png").toURI().toString(), iconSenate.getWidth(), iconSenate.getHeight(), true, false)));
        iconLighthouse.setFill(new ImagePattern(new Image(new File("res/images/buildings/faro.png").toURI().toString(), iconSenate.getWidth(), iconSenate.getHeight(), true, false)));
        iconLibrary.setFill(new ImagePattern(new Image(new File("res/images/buildings/biblioteca.png").toURI().toString(), iconSenate.getWidth(), iconSenate.getHeight(), true, false)));
        iconOracle.setFill(new ImagePattern(new Image(new File("res/images/buildings/oraculo.png").toURI().toString(), iconSenate.getWidth(), iconSenate.getHeight(), true, false)));
        iconDivineStatue.setFill(new ImagePattern(new Image(new File("res/images/buildings/estatua_divina.png").toURI().toString(), iconSenate.getWidth(), iconSenate.getHeight(), true, false)));
        iconMerchants.setFill(new ImagePattern(new Image(new File("res/images/buildings/oficina_comercial.png").toURI().toString(), iconSenate.getWidth(), iconSenate.getHeight(), true, false)));

        iconPlow.setFill(new ImagePattern(new Image(new File("res/images/tecnologies/arado.png").toURI().toString(), iconPlow.getWidth(), iconPlow.getHeight(), true, false)));
        iconBunks.setFill(new ImagePattern(new Image(new File("res/images/tecnologies/literas.png").toURI().toString(), iconPlow.getWidth(), iconPlow.getHeight(), true, false)));

        iconZeus.setFill(new ImagePattern(new Image(new File("res/images/gods/zeus.png").toURI().toString(), iconZeus.getWidth(), iconZeus.getHeight(), true, false)));
        iconAthena.setFill(new ImagePattern(new Image(new File("res/images/gods/atenea.png").toURI().toString(), iconZeus.getWidth(), iconZeus.getHeight(), true, false)));
        iconPoseidon.setFill(new ImagePattern(new Image(new File("res/images/gods/poseidon.png").toURI().toString(), iconZeus.getWidth(), iconZeus.getHeight(), true, false)));
        iconHera.setFill(new ImagePattern(new Image(new File("res/images/gods/hera.png").toURI().toString(), iconZeus.getWidth(), iconZeus.getHeight(), true, false)));
        iconArtemis.setFill(new ImagePattern(new Image(new File("res/images/gods/artemisa.png").toURI().toString(), iconZeus.getWidth(), iconZeus.getHeight(), true, false)));
        iconHades.setFill(new ImagePattern(new Image(new File("res/images/gods/hades.png").toURI().toString(), iconZeus.getWidth(), iconZeus.getHeight(), true, false)));
        iconAphrodite.setFill(new ImagePattern(new Image(new File("res/images/gods/afrodita.png").toURI().toString(), iconZeus.getWidth(), iconZeus.getHeight(), true, false)));
        iconAres.setFill(new ImagePattern(new Image(new File("res/images/gods/ares.png").toURI().toString(), iconZeus.getWidth(), iconZeus.getHeight(), true, false)));

        // UNITS

        iconSwordsman.setFill(new ImagePattern(new Image(new File("res/images/units/land/infante.png").toURI().toString(), iconSwordsman.getWidth(), iconSwordsman.getHeight(), true, false)));
        iconSlinger.setFill(new ImagePattern(new Image(new File("res/images/units/land/hondero.png").toURI().toString(), iconSwordsman.getWidth(), iconSwordsman.getHeight(), true, false)));
        iconArcher.setFill(new ImagePattern(new Image(new File("res/images/units/land/arquero.png").toURI().toString(), iconSwordsman.getWidth(), iconSwordsman.getHeight(), true, false)));
        iconHorseman.setFill(new ImagePattern(new Image(new File("res/images/units/land/caballero.png").toURI().toString(), iconSwordsman.getWidth(), iconSwordsman.getHeight(), true, false)));
        iconHoplite.setFill(new ImagePattern(new Image(new File("res/images/units/land/hoplita.png").toURI().toString(), iconSwordsman.getWidth(), iconSwordsman.getHeight(), true, false)));
        iconChariot.setFill(new ImagePattern(new Image(new File("res/images/units/land/carro.png").toURI().toString(), iconSwordsman.getWidth(), iconSwordsman.getHeight(), true, false)));
        iconCatapult.setFill(new ImagePattern(new Image(new File("res/images/units/land/catapulta.png").toURI().toString(), iconSwordsman.getWidth(), iconSwordsman.getHeight(), true, false)));
        iconDivineEnvoy.setFill(new ImagePattern(new Image(new File("res/images/units/mythics/land/enviado_divino.png").toURI().toString(), iconSwordsman.getWidth(), iconSwordsman.getHeight(), true, false)));


        iconBireme.setFill(new ImagePattern(new Image(new File("res/images/units/sea/birreme.png").toURI().toString(), iconSwordsman.getWidth(), iconSwordsman.getHeight(), true, false)));
        iconLightShip.setFill(new ImagePattern(new Image(new File("res/images/units/sea/mecha.png").toURI().toString(), iconSwordsman.getWidth(), iconSwordsman.getHeight(), true, false)));
        iconFireShip.setFill(new ImagePattern(new Image(new File("res/images/units/sea/brulote.png").toURI().toString(), iconSwordsman.getWidth(), iconSwordsman.getHeight(), true, false)));
        iconTrireme.setFill(new ImagePattern(new Image(new File("res/images/units/sea/trirreme.png").toURI().toString(), iconSwordsman.getWidth(), iconSwordsman.getHeight(), true, false)));
        iconColony.setFill(new ImagePattern(new Image(new File("res/images/units/sea/colono.png").toURI().toString(), iconSwordsman.getWidth(), iconSwordsman.getHeight(), true, false)));
        iconSlowBoat.setFill(new ImagePattern(new Image(new File("res/images/units/sea/bote_lento.png").toURI().toString(), iconSwordsman.getWidth(), iconSwordsman.getHeight(), true, false)));
        iconFastBoat.setFill(new ImagePattern(new Image(new File("res/images/units/sea/bote_rapido.png").toURI().toString(), iconSwordsman.getWidth(), iconSwordsman.getHeight(), true, false)));


    }
}
