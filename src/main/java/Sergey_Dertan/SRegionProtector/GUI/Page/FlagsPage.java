package Sergey_Dertan.SRegionProtector.GUI.Page;

import Sergey_Dertan.SRegionProtector.Region.Flags.Flag.RegionFlag;
import Sergey_Dertan.SRegionProtector.Region.Flags.Flag.RegionSellFlag;
import Sergey_Dertan.SRegionProtector.Region.Flags.Flag.RegionTeleportFlag;
import Sergey_Dertan.SRegionProtector.Region.Flags.RegionFlags;
import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.Utils.Tags;
import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.Tag;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public final class FlagsPage implements Page {

    @Override
    public Map<Integer, Item> getItems(Region region, int page) {
        Map<Integer, Item> list = new HashMap<>();
        int counter = 0;
        for (RegionFlag flag : Arrays.stream(region.getFlags()).skip(page * 18).limit(18).collect(Collectors.toList())) {
            Item item = Item.get(ItemID.BANNER);

            String name = RegionFlags.getFlagName(counter + page * 18);
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
            item.setCustomName(name);

            String[] lore = new String[2];
            lore[0] = "Value: " + (region.getFlagState(counter + page * 18) == RegionFlags.getStateFromString("allow", counter + page * 18) ? "allow" : "deny");
            if (flag instanceof RegionSellFlag) {
                lore[1] = "Price: " + ((RegionSellFlag) flag).price;
            } else if (flag instanceof RegionTeleportFlag) {
                Vector3 pos = ((RegionTeleportFlag) flag).position;
                if (pos != null) {
                    lore[1] = "x: " + Math.round(pos.x) + ", y: " + Math.round(pos.y) + ", z: " + Math.round(pos.z);
                }
            }
            item.setLore(lore[1] == null ? new String[]{lore[0]} : lore);
            CompoundTag nbt = item.getNamedTag();
            nbt.putInt(Tags.FLAG_ID_TAG, counter + page * 18);
            list.put(counter, item);
            ++counter;
        }
        this.addNavigators(list);
        this.prepareItems(list.values(), page);
        return list;
    }

    @Override
    public boolean handle(Item item, Region region, Player player) {
        Tag tag = item.getNamedTagEntry(Tags.FLAG_ID_TAG);
        if (!(tag instanceof IntTag)) return false;
        int flagId = ((IntTag) tag).data;
        if (flagId == RegionFlags.FLAG_SELL || flagId == RegionFlags.FLAG_TELEPORT) return false;
        if (!RegionFlags.hasFlagPermission(player, flagId)) return false;
        region.setFlagState(flagId, !region.getFlagState(flagId));
        return true;
    }

    @Override
    public String getName() {
        return "flags";
    }
}
