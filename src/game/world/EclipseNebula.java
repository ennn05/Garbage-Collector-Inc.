package game.world;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.World;
import game.actors.ContractedWorker;
import game.enums.AccessLevel;
import game.flora.FleshyMatureTree;
import game.flora.FleshySapling;
import game.flora.FleshySprout;
import game.flora.WarperMatureTree;
import game.flora.WarperSapling;
import game.grounds.AluminiumDoor;
import game.grounds.Dirt;
import game.grounds.Fire;
import game.grounds.Floor;
import game.grounds.Hole;
import game.grounds.IronDoor;
import game.grounds.MagicCircle;
import game.grounds.Puddle;
import game.grounds.Supercomputer;
import game.grounds.TeleportationTube;
import game.grounds.TitaniumDoor;
import game.grounds.ToxicWaste;
import game.grounds.Trap;
import game.grounds.Wall;
import game.inventory.WeightLimitedInventory;
import game.items.AccessCard;
import game.items.AlienCube;
import game.items.Apple;
import game.items.CRTMonitor;
import game.items.Cookies;
import game.items.FirstAidKit;
import game.items.Flask;
import game.items.FloppyDisk;
import game.items.Lantern;
import game.items.SterilisationBox;
<<<<<<< HEAD
=======
import game.grounds.Supercomputer;
import game.inventory.WeightLimitedInventory;
import game.items.AccessCard;
import game.items.AlienCube;
import game.enums.AccessLevel;
import game.grounds.Wall;
import game.flora.FleshySprout;
import game.flora.FleshySapling;
import game.flora.FleshyMatureTree;
import game.flora.WarperSapling;
import game.flora.WarperMatureTree;
import game.actors.Mannequin;
>>>>>>> REQ5

import java.util.Arrays;
import java.util.List;

/**
 * This class handles the miracle of creation, translating a bunch of periods
 * and hashtags into a sprawling, functional sci-fi facility.
 */
public class EclipseNebula extends World {
    public EclipseNebula(Display display) {
        super(display);
    }

    private Inventory createStarterInventory() {
        Inventory inventory = new WeightLimitedInventory(50);
        inventory.add(new Flask());
        return inventory;
    }

    private ContractedWorker createWorker(String name) {
        return new ContractedWorker(name, 'ඞ', 10, createStarterInventory());
    }

    public void initialise() throws Exception {
        DefaultGroundCreator groundCreator = new DefaultGroundCreator();
        groundCreator.registerGround('.', Dirt::new);
        groundCreator.registerGround('#', Wall::new);
        groundCreator.registerGround('~', Puddle::new);
        groundCreator.registerGround('_', Floor::new);
        groundCreator.registerGround('=', AluminiumDoor::new);
        groundCreator.registerGround('N', IronDoor::new);
        groundCreator.registerGround('M', TitaniumDoor::new);
        groundCreator.registerGround('^', () -> new Fire(new Floor()));
        groundCreator.registerGround('o', Hole::new);
        groundCreator.registerGround('!', Trap::new);

        // REQ2 ground types
        groundCreator.registerGround('≡', Supercomputer::new);
        groundCreator.registerGround('≈', ToxicWaste::new);
        groundCreator.registerGround('Φ', TeleportationTube::new);
        groundCreator.registerGround('◎', MagicCircle::new);
        groundCreator.registerGround('◈', Floor::new);

        // REQ3 flora ground types
        groundCreator.registerGround('y', FleshySprout::new);
        groundCreator.registerGround('v', FleshySapling::new);
        groundCreator.registerGround('Y', FleshyMatureTree::new);
        groundCreator.registerGround('w', WarperSapling::new);
        groundCreator.registerGround('W', WarperMatureTree::new);

        List<String> moon99Deprecated = Arrays.asList(
                "....................########################################",
                "...#######..........#__________________#___________________#",
                "...#_____#..........=__________________=___________________#",
                "...#___Φ_=...~......#__________________#___________________#",
                "...#_____#..~~~.....########=#####=#####___#############___#",
                "...#######.~~~~.....#______#_#_________#___#___________#___#",
                ".........~~~~.......#______#_#_________#####___________#####",
                "....................#______=_#_________#___________________#",
                "......~.............#______#_#_________#___________________#",
                ".....~~~............#______#_###########___#############___#",
                ".....~..............#______#___________#___#___________#___#",
                "....................=______#___________=___=___________=___#",
                "....................#______#############___#############___#",
                ".........~~~~.......#______#___________#####################",
                "........~~~~~~......#______#___________=___________________#",
                ".........~~~~.......#______#___________#___________________#",
                "....................#______#############___#############___#",
                "....................#______#___________#___#___________#___#",
                "..~.................#______=___________=___=___________=___#",
                "....................########################################"
        );

        GameMap moon99DeprecatedMap = new GameMap("99-Deprecated", groundCreator, moon99Deprecated);
        this.addGameMap(moon99DeprecatedMap);

        moon99DeprecatedMap.at(5, 3).setGround(new Supercomputer());

        FacilityAlarmSystem.register(moon99DeprecatedMap);

        // Set up Teleportation Tube in the ship with destinations.
        TeleportationTube ship99Tube = new TeleportationTube();
        ship99Tube.addDestination(moon99DeprecatedMap.at(30, 10));
        ship99Tube.addDestination(moon99DeprecatedMap.at(45, 10));
        moon99DeprecatedMap.at(7, 3).setGround(ship99Tube);

        AccessCard accessCard = new AccessCard(AccessLevel.LEVEL_1);
        FirstAidKit firstAidKit = new FirstAidKit();
        SterilisationBox sterilisationBox = new SterilisationBox();

        moon99DeprecatedMap.at(7, 2).addItem(accessCard);
        moon99DeprecatedMap.at(7, 3).addItem(firstAidKit);
        moon99DeprecatedMap.at(7, 4).addItem(sterilisationBox);

        moon99DeprecatedMap.at(56, 1).addItem(new Apple());
        moon99DeprecatedMap.at(49, 11).addItem(new Cookies());
        moon99DeprecatedMap.at(33, 17).addItem(new Lantern());
        moon99DeprecatedMap.at(57, 17).addItem(new FloppyDisk());
        moon99DeprecatedMap.at(57, 17).addItem(new CRTMonitor());

        moon99DeprecatedMap.at(30, 3).setGround(new Hole());
        moon99DeprecatedMap.at(44, 9).setGround(new Hole());
        moon99DeprecatedMap.at(24, 15).setGround(new Hole());

        moon99DeprecatedMap.at(23, 2).setGround(new Trap());
        moon99DeprecatedMap.at(47, 8).setGround(new Trap());
        moon99DeprecatedMap.at(35, 14).setGround(new Trap());

        ContractedWorker contractedWorker1 = createWorker("#1 Bob");
        ContractedWorker contractedWorker2 = createWorker("#2 Tom");
        ContractedWorker contractedWorker3 = createWorker("#3 Sarah");
        ContractedWorker contractedWorker4 = createWorker("#4 Julie");
        ContractedWorker contractedWorker5 = createWorker("#5 Rick");

        this.addPlayer(contractedWorker1, moon99DeprecatedMap.at(6, 2));
        this.addPlayer(contractedWorker2, moon99DeprecatedMap.at(7, 2));
        this.addPlayer(contractedWorker3, moon99DeprecatedMap.at(8, 2));
        this.addPlayer(contractedWorker4, moon99DeprecatedMap.at(6, 4));
        this.addPlayer(contractedWorker5, moon99DeprecatedMap.at(8, 4));

        // Create 20-Overflow map.
        List<String> overflow20 = Arrays.asList(
                ".....................≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈",
                "...#######...........≈≈≈≈≈≈≈≈≈≈≈≈≈≈##################≈≈≈≈≈≈≈",
                "...#≡____#...........≈≈≈≈≈≈≈≈≈≈≈≈≈≈#________________#≈≈≈≈≈≈≈",
                "...#__Φ__=...........≈≈≈≈≈≈≈≈#######_______◈________#≈≈≈≈≈≈≈",
                "...#_____#...........≈≈≈≈≈≈≈≈#_____=________________#≈≈≈≈≈≈≈",
                "...#######...≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈#_◎___###########=######≈≈≈≈≈≈≈",
                ".............≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈#_____#≈≈≈≈≈≈≈≈≈#______#≈≈≈≈≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈#########=#####≈≈≈≈≈≈≈≈≈#______#≈≈≈≈≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈#_____________#≈≈≈≈≈≈≈≈≈#___◎__#≈≈≈≈≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈#______o______#≈≈≈≈≈≈≈≈≈#______#≈≈≈≈≈≈≈",
                ".............≈≈≈≈≈≈≈≈######=########≈≈≈≈≈≈≈≈≈####=###≈≈≈≈≈≈≈",
                "...≈≈≈≈≈≈≈≈≈.≈≈≈≈≈≈≈≈≈≈≈≈≈#_#≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈#_#≈≈≈≈≈≈≈≈≈",
                "...≈≈≈≈≈≈≈≈≈.≈≈≈≈≈≈≈≈≈≈≈≈≈#_#≈≈≈≈≈###############_#######≈≈≈",
                ".............≈≈≈≈≈≈≈≈≈≈≈≈≈#_____________________________#≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈≈≈≈≈≈#_______=__________◈__≈≈≈≈____#≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈≈≈≈≈≈#___◎___#_____________≈≈≈≈≈≈__≈≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈≈≈≈≈≈######################≈≈≈≈≈≈≈≈≈≈≈≈",
                ".............≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈",
                ".....................≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈",
                ".....................≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈"
        );

        GameMap overflow20Map = new GameMap("20-Overflow", groundCreator, overflow20);
        this.addGameMap(overflow20Map);

        FacilityAlarmSystem.register(overflow20Map);

        // Place Alien Cubes at marked locations.
        overflow20Map.at(17, 3).addItem(new AlienCube());
        overflow20Map.at(18, 14).addItem(new AlienCube());

        // Set up Teleportation Tube at (6, 3) with destinations.
        TeleportationTube overflow20Tube = new TeleportationTube();
        overflow20Tube.addDestination(overflow20Map.at(15, 3));
        overflow20Tube.addDestination(overflow20Map.at(15, 8));
        overflow20Tube.addDestination(overflow20Map.at(15, 14));
        overflow20Tube.addDestination(moon99DeprecatedMap.at(30, 10));
        overflow20Map.at(6, 3).setGround(overflow20Tube);

        // Add a cross-map destination to the ship tube in 99-deprecated.
        ship99Tube.addDestination(overflow20Map.at(6, 3));

        // REQ3: Populate the 20-Overflow moon with mutated flora.
        overflow20Map.at(1, 0).setGround(new FleshySprout());
        overflow20Map.at(5, 0).setGround(new FleshySprout());
        overflow20Map.at(9, 0).setGround(new FleshySprout());
        overflow20Map.at(1, 6).setGround(new FleshySprout());
        overflow20Map.at(1, 10).setGround(new FleshySprout());

        overflow20Map.at(13, 0).setGround(new FleshySapling());
        overflow20Map.at(5, 6).setGround(new FleshySapling());

        overflow20Map.at(17, 0).setGround(new FleshyMatureTree());
        overflow20Map.at(9, 6).setGround(new FleshyMatureTree());

        overflow20Map.at(1, 17).setGround(new WarperSapling());
        overflow20Map.at(5, 17).setGround(new WarperSapling());
        overflow20Map.at(1, 19).setGround(new WarperSapling());

        overflow20Map.at(5, 19).setGround(new WarperMatureTree());
        overflow20Map.at(9, 19).setGround(new WarperMatureTree());
<<<<<<< HEAD
=======

        // REQ5: Mannequins placed in floor areas of the 20-Overflow map
        overflow20Map.addActor(new Mannequin(), overflow20Map.at(17, 4));
        overflow20Map.addActor(new Mannequin(), overflow20Map.at(28, 8));

        // Add a cross-map destination to the ship tube in 99-deprecated.
        // This ensures the tubes support both within-map and between-maps movement.
        if (moon99DeprecatedMap.at(7, 3).getGround() instanceof TeleportationTube) {
            TeleportationTube ship99Tube = (TeleportationTube) moon99DeprecatedMap.at(7, 3).getGround();
            ship99Tube.addDestination(overflow20Map.at(6, 3));
        }
>>>>>>> REQ5
    }
}