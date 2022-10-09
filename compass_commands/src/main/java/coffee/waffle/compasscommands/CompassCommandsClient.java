package coffee.waffle.compasscommands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandBuildContext;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.DefaultPosArgument;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.quiltmc.qsl.command.api.client.ClientCommandManager;
import org.quiltmc.qsl.command.api.client.ClientCommandRegistrationCallback;
import org.quiltmc.qsl.command.api.client.QuiltClientCommandSource;

import static org.quiltmc.qsl.command.api.client.ClientCommandManager.literal;

public class CompassCommandsClient implements ClientCommandRegistrationCallback {
  private static final int LIMIT = 29999872;
  private static final MinecraftClient client = MinecraftClient.getInstance();

  @Override
  public void registerCommands(CommandDispatcher<QuiltClientCommandSource> dispatcher, CommandBuildContext buildContext, CommandManager.RegistrationEnvironment environment) {
    LiteralCommandNode<QuiltClientCommandSource> compassCommand = dispatcher.register(literal("compass")
            .then(literal("set").then(ClientCommandManager.argument("pos", BlockPosArgumentType.blockPos())
                    .executes(c -> {
                      ServerCommandSource fakeSource = new ServerCommandSource(null, client.player.getPos(), null, null, 0, null, null, null, null);
                      return setTarget(c.getArgument("pos", DefaultPosArgument.class).toAbsolutePos(fakeSource));
                    })))
            .then(literal("north").executes(c -> setTarget(0, -LIMIT)))
            .then(literal("south").executes(c -> setTarget(0, LIMIT)))
            .then(literal("east").executes(c -> setTarget(LIMIT, 0)))
            .then(literal("west").executes(c -> setTarget(-LIMIT, 0)))
            .then(literal("northwest").executes(c -> setTarget(-LIMIT, -LIMIT)))
            .then(literal("northeast").executes(c -> setTarget(LIMIT, -LIMIT)))
            .then(literal("southwest").executes(c -> setTarget(-LIMIT, LIMIT)))
            .then(literal("southeast").executes(c -> setTarget(LIMIT, LIMIT)))
            .then(literal("spawn").executes(c -> setTarget(c.getSource().getPlayer().clientWorld.getSpawnPos())))
            .then(literal("current").executes(c -> setTarget(c.getSource().getPlayer().getPos())))
    );
    dispatcher.register(literal("comp").redirect(compassCommand));
  }

  private static int setTarget(Vec3d vec) {
    return setTarget(new BlockPos(vec));
  }

  private static int setTarget(int x, int z) {
    return setTarget(new BlockPos(x, 64, z));
  }

  private static int setTarget(BlockPos pos) {
    client.world.setSpawnPos(pos, 0);
    client.player.sendMessage(Text.translatable("msg.compass-commands.compass-target", pos.getX(), pos.getY(), pos.getZ()), true);
    return 1;
  }
}
