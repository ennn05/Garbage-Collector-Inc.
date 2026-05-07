package game.world;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.World;
import game.actors.ContractedWorker;
import game.grounds.Dirt;
import game.grounds.Door;
import game.grounds.Fire;
import game.grounds.Floor;
import game.grounds.Hole;
import game.grounds.Puddle;
import game.grounds.Trap;
import game.grounds.Wall;
import game.inventory.WeightLimitedInventory;
import game.items.AccessCard;
import game.items.Apple;
import game.items.Cookies;
import game.items.CRTMonitor;
import game.items.FirstAidKit;
import game.items.Flask;
import game.items.FloppyDisk;
import game.items.Lantern;
import game.items.SterilisationBox;

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
        groundCreator.registerGround('=', Door::new);
        groundCreator.registerGround('^', () -> new Fire(new Floor()));
        groundCreator.registerGround('ò', Hole::new);
        groundCreator.registerGround('!', Trap::new);

        List<String> moon99Deprecated = Arrays.asList(
                "....................########################################",
                "...#######..........#__________________#___________________#",
                "...#_____#..........=__________________=___________________#",
                "...#_____=...~......#__________________#___________________#",
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

        FacilityAlarmSystem.register(moon99DeprecatedMap); // each map register one alarm system

        AccessCard accessCard = new AccessCard();
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

        moon99DeprecatedMap.at(23, 2).setGround(new Trap()); // trap 1
        moon99DeprecatedMap.at(47, 8).setGround(new Trap()); // trap 2
        moon99DeprecatedMap.at(35, 14).setGround(new Trap()); // trap 3

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
    }
}