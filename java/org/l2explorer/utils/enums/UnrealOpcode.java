package org.l2explorer.utils.enums;

import java.util.HashMap;
import java.util.Map;
import org.l2explorer.unreal.bytecode.token.*;

public class UnrealOpcode {

    public enum Main {
        LOCAL_VARIABLE(0x00, LocalVariable.class, "LocalVariable"),
        INSTANCE_VARIABLE(0x01, InstanceVariable.class, "InstanceVariable"),
        DEFAULT_VARIABLE(0x02, DefaultVariable.class, "DefaultVariable"),
        STATE_VARIABLE(0x03, StateVariable.class, "StateVariable"),
        RETURN(0x04, Return.class, "Return"),
        SWITCH(0x05, Switch.class, "Switch"),
        JUMP(0x06, Jump.class, "Jump"),
        JUMP_IF_NOT(0x07, JumpIfNot.class, "JumpIfNot"),
        STOP(0x08, Stop.class, "Stop"),
        ASSERT(0x09, Assert.class, "Assert"),
        CASE(0x0a, Case.class, "Case"),
        NOTHING(0x0b, Nothing.class, "Nothing"),
        LABEL_TABLE(0x0c, LabelTable.class, "LabelTable"),
        GOTO_LABEL(0x0d, GotoLabel.class, "GotoLabel"),        
        EAT_STRING(0x0e, EatString.class, "EatString"),
        LET(0x0f, Let.class, "Let"),
        DYN_ARRAY_ELEMENT(0x10, DynArrayElement.class, "DynArrayElement"),
        NEW(0x11, New.class, "New"),
        CLASS_CONTEXT(0x12, ClassContext.class, "ClassContext"),
        METACAST(0x13, Metacast.class, "Metacast"),
        LET_BOOL(0x14, LetBool.class, "LetBool"),
        END_PARM_VALUES(0x15, EndParmValues.class, "EndParmValues"),
        END_FUNCTION_PARAMS(0x16, EndFunctionParams.class, "EndFunctionParams"),
        SELF(0x17, Self.class, "Self"),
        SKIP(0x18, Skip.class, "Skip"),
        CONTEXT(0x19, Context.class, "Context"),
        ARRAY_ELEMENT(0x1a, ArrayElement.class, "ArrayElement"),
        VIRTUAL_FUNCTION(0x1b, VirtualFunction.class, "VirtualFunction"),
        FINAL_FUNCTION(0x1c, FinalFunction.class, "FinalFunction"),
        INT_CONST(0x1d, IntConst.class, "IntConst"),
        FLOAT_CONST(0x1e, FloatConst.class, "FloatConst"),
        STRING_CONST(0x1f, StringConst.class, "StringConst"),
        OBJECT_CONST(0x20, ObjectConst.class, "ObjectConst"),
        NAME_CONST(0x21, NameConst.class, "NameConst"),
        ROTATOR_CONST(0x22, RotatorConst.class, "RotatorConst"),
        VECTOR_CONST(0x23, VectorConst.class, "VectorConst"),
        BYTE_CONST(0x24, ByteConst.class, "ByteConst"),
        INT_ZERO(0x25, IntZero.class, "IntZero"),
        INT_ONE(0x26, IntOne.class, "IntOne"),
        TRUE(0x27, True.class, "True"),
        FALSE(0x28, False.class, "False"),
        NATIVE_PARAM(0x29, NativeParam.class, "NativeParam"),
        NO_OBJECT(0x2a, NoObject.class, "NoObject"),
        DELEGATE_FUNCTION(0x2b, DelegateFunction.class, "DelegateFunction"),
        INT_CONST_BYTE(0x2c, IntConstByte.class, "IntConstByte"),
        BOOL_VARIABLE(0x2d, BoolVariable.class, "BoolVariable"),
        DYNAMIC_CAST(0x2e, DynamicCast.class, "DynamicCast"),
        ITERATOR(0x2f, Iterator.class, "Iterator"),
        ITERATOR_POP(0x30, IteratorPop.class, "IteratorPop"),
        ITERATOR_NEXT(0x31, IteratorNext.class, "IteratorNext"),
        STRUCT_CMP_EQ(0x32, StructCmpEq.class, "StructCmpEq"),
        STRUCT_CMP_NE(0x33, StructCmpNe.class, "StructCmpNe"),
        STRUCT_CONST(0x34, StructConst.class, "StructConst"),               
        RANGE_CONST(0x35, RangeConst.class, "RangeConst"),                  
        STRUCT_MEMBER(0x36, StructMember.class, "StructMember"),
        LENGTH(0x37, Length.class, "Length"),
        GLOBAL_FUNCTION(0x38, GlobalFunction.class, "GlobalFunction"),
        CONVERSION_TABLE(0x39, ConversionTable.class, "ConversionTable"),
        INSERT(0x40, Insert.class, "Insert"),
        REMOVE(0x41, Remove.class, "Remove"),
        DELEGATE_NAME(0x44, DelegateName.class, "DelegateName"),                
        INT64_CONST(0x46, INT64Const.class, "INT64Const"),                     
        DYN_ARRAY_SORT(0x47, DynArraySort.class, "DynArraySort"),
        // Conversion (0x3a-0x63)
        BYTE_TO_INT(0x3a, ByteToInt.class, "ByteToInt"),
        BYTE_TO_BOOL(0x3b, ByteToBool.class, "ByteToBool"),
        BYTE_TO_FLOAT(0x3c, ByteToFloat.class, "ByteToFloat"),
        INT_TO_BYTE(0x3d, IntToByte.class, "IntToByte"),
        INT_TO_BOOL(0x3e, IntToBool.class, "IntToBool"),
        INT_TO_FLOAT(0x3f, IntToFloat.class, "IntToFloat"),
        BOOL_TO_BYTE(0x40, BoolToByte.class, "BoolToByte"),                    
        BOOL_TO_INT(0x41, BoolToInt.class, "BoolToInt"),                       
        BOOL_TO_FLOAT(0x42, BoolToFloat.class, "BoolToFloat"),
        FLOAT_TO_BYTE(0x43, FloatToByte.class, "FloatToByte"),
        FLOAT_TO_INT(0x44, FloatToInt.class, "FloatToInt"),                     
        FLOAT_TO_BOOL(0x45, FloatToBool.class, "FloatToBool"),
        STRING_TO_NAME(0x46, StringToName.class, "StringToName"),               
        OBJECT_TO_BOOL(0x47, ObjectToBool.class, "ObjectToBool"),               
        NAME_TO_BOOL(0x48, NameToBool.class, "NameToBool"),
        STRING_TO_BYTE(0x49, StringToByte.class, "StringToByte"),
        STRING_TO_INT(0x4a, StringToInt.class, "StringToInt"),
        STRING_TO_BOOL(0x4b, StringToBool.class, "StringToBool"),
        STRING_TO_FLOAT(0x4c, StringToFloat.class, "StringToFloat"),
        STRING_TO_VECTOR(0x4d, StringToVector.class, "StringToVector"),
        STRING_TO_ROTATOR(0x4e, StringToRotator.class, "StringToRotator"),
        VECTOR_TO_BOOL(0x4f, VectorToBool.class, "VectorToBool"),
        VECTOR_TO_ROTATOR(0x50, VectorToRotator.class, "VectorToRotator"),
        ROTATOR_TO_BOOL(0x51, RotatorToBool.class, "RotatorToBool"),
        BYTE_TO_STRING(0x52, ByteToString.class, "ByteToString"),
        INT_TO_STRING(0x53, IntToString.class, "IntToString"),
        BOOL_TO_STRING(0x54, BoolToString.class, "BoolToString"),
        FLOAT_TO_STRING(0x55, FloatToString.class, "FloatToString"),
        OBJECT_TO_STRING(0x56, ObjectToString.class, "ObjectToString"),
        NAME_TO_STRING(0x57, NameToString.class, "NameToString"),
        VECTOR_TO_STRING(0x58, VectorToString.class, "VectorToString"),
        ROTATOR_TO_STRING(0x59, RotatorToString.class, "RotatorToString"),
        BYTE_TO_INT64(0x5a, ByteToINT64.class, "ByteToINT64"),
        MBYTE_TO_INT64 (0x5a, MByteToINT64.class, "MByteToINT64"),
        INT_TO_INT64(0x5b, IntToINT64.class, "IntToINT64"),
        BOOL_TO_INT64(0x5c, BoolToINT64.class, "BoolToINT64"),
        FLOAT_TO_INT64(0x5d, FloatToINT64.class, "FloatToINT64"),
        STRING_TO_INT64(0x5e, StringToINT64.class, "StringToINT64"),
        INT64_TO_BYTE(0x5f, INT64ToByte.class, "INT64ToByte"),
        INT64_TO_INT(0x60, INT64ToInt.class, "INT64ToInt"),
        INT64_TO_BOOL(0x61, INT64ToBool.class, "INT64ToBool"),
        INT64_TO_FLOAT(0x62, INT64ToFloat.class, "INT64ToFloat"),
        INT64_TO_STRING(0x63, INT64ToString.class, "INT64ToString");
    	
    	// Under test
    	/* INTERFACE_CAST(0x64, InterfaceCast.class, "InterfaceCast");
    	   SWITCH_LABEL(0x65, SwitchLabel.class, "SwitchLabel");
    	   JUMP_IF_NOT_EQUAL(0x66, JumpIfNotEqual.class, "JumpInNotEqual");
    	   SAFE_GET_CLASS(0x67,  SafeGetClass.class, "SafeGetClass");
    	   NATIVE_FUNCTION(0x68, NativeFunction.class, "NativeFunction");
    	   EQUAL_DELEGATE(0x69, EqualDelegate.class, "EqualDelegate");
    	   NOT_EQUAL_DELEGATE(0x6a, NotEqualDelegate.class, "NotEqualDelegate");
    	   INT_CONST_PLUS(0x6b, IntConstPlus.class, "IntConstPlus"):
    	   LET_DELEGATE(0x6c, LetDelegate.class, "LetDelegate");
    	   CLEAR_DELEGATE(0x6d, ClearDelegate.class, "ClearDelegate");
    	   LET_FUNCTION(0x6e, LetFunction.class, "LetFunction");
    	   STRUCT_MEMBER_REF(0x6f, StructMemberRef.class, "StructMemberRef");
    	   DYN_ARRAY_ADD(0x70, DynArrayAdd.class, "DynArrayAdd");
    	   // Dynamic Array Operations
		   DYN_ARRAY_INSERT(0x71, DynArrayInsert.class, "DynArrayInsert"),
		   DYN_ARRAY_REMOVE(0x72, DynArrayRemove.class, "DynArrayRemove"),
		   DYN_ARRAY_FIND(0x73, DynArrayFind.class, "DynArrayFind"),
		   DYN_ARRAY_RESIZE(0x74, DynArrayResize.class, "DynArrayResize"),
		   DYN_ARRAY_ADD_ITEM(0x75, DynArrayAddItem.class, "DynArrayAddItem"),
		   DYN_ARRAY_REMOVE_ITEM(0x76, DynArrayRemoveItem.class, "DynArrayRemoveItem"),
			
		   // Map Operations
		   MAP_CONST(0x77, MapConst.class, "MapConst"),
		   MAP_FIND(0x78, MapFind.class, "MapFind"),
		   MAP_ADD(0x79, MapAdd.class, "MapAdd"),
		   MAP_REMOVE(0x7A, MapRemove.class, "MapRemove"),
		   MAP_CLEAR(0x7B, MapClear.class, "MapClear"),
			
		   // Internal/Reserved
		   RESERVED_7C(0x7C, Reserved7C.class, "Reserved7C"),
		   RESERVED_7D(0x7D, Reserved7D.class, "Reserved7D"),
		   RESERVED_7E(0x7E, Reserved7E.class, "Reserved7E"),
		   INTERNAL_DEBUG(0x7F, InternalDebug.class, "InternalDebug"),
			
		   // Lineage2 Specific Tokens
		   QUEST_UPDATE(0x80, QuestUpdate.class, "QuestUpdate"),
		   SKILL_USE(0x81, SkillUse.class, "SkillUse"),
		   ITEM_USE(0x82, ItemUse.class, "ItemUse"),
		   SOCIAL_ACTION(0x83, SocialAction.class, "SocialAction"),
		   TARGET_SELECT(0x84, TargetSelect.class, "TargetSelect"),
		   PARTY_COMMAND(0x85, PartyCommand.class, "PartyCommand"),
		   CLAN_COMMAND(0x86, ClanCommand.class, "ClanCommand"),
		   TRADE(0x87, Trade.class, "Trade"),
		   MANUFACTURE(0x88, Manufacture.class, "Manufacture"),
		   ENCHANT(0x89, Enchant.class, "Enchant"),
		   AUGMENT(0x8A, Augment.class, "Augment"),
		   PLEDGE(0x8B, Pledge.class, "Pledge"),
		   SUB_PLEDGE(0x8C, SubPledge.class, "SubPledge"),
		   WAREHOUSE(0x8D, Warehouse.class, "Warehouse"),
		   MAIL(0x8E, Mail.class, "Mail"),
		   OBSERVATION(0x8F, Observation.class, "Observation"),
		   PET_COMMAND(0x90, PetCommand.class, "PetCommand"),
		   SUMMON_COMMAND(0x91, SummonCommand.class, "SummonCommand"),
		   BUFF_MANAGEMENT(0x92, BuffManagement.class, "BuffManagement"),
		   RECIPE_SYSTEM(0x93, RecipeSystem.class, "RecipeSystem"),
		   TRANSFORMATION(0x94, Transformation.class, "Transformation"),
		   TELEPORT(0x95, Teleport.class, "Teleport"),
		   PRIVATE_STORE(0x96, PrivateStore.class, "PrivateStore"),
		   BOT_REPORT(0x97, BotReport.class, "BotReport"),
		   ATTRIBUTE_SYSTEM(0x98, AttributeSystem.class, "AttributeSystem"),
		   ELEMENTAL_SYSTEM(0x99, ElementalSystem.class, "ElementalSystem"),
		   ACHIEVEMENT_SYSTEM(0x9A, AchievementSystem.class, "AchievementSystem"),
		   HERO_SYSTEM(0x9B, HeroSystem.class, "HeroSystem"),
		   OLYMPIAD(0x9C, Olympiad.class, "Olympiad"),
		   INSTANCE_ZONE(0x9D, InstanceZone.class, "InstanceZone"),
		   DUAL_CLASS(0x9E, DualClass.class, "DualClass"),
		   AUTO_HUNT(0x9F, AutoHunt.class, "AutoHunt");
		   // Modern L2 Systems (Essence)
			UI_UPDATE(0xA0, UiUpdate.class, "UiUpdate"),
			QUICK_BAR(0xA1, QuickBar.class, "QuickBar"),
			MACRO_SYSTEM(0xA2, MacroSystem.class, "MacroSystem"),
			CONFIG_UPDATE(0xA3, ConfigUpdate.class, "ConfigUpdate"),
			ANTI_BOT(0xA4, AntiBot.class, "AntiBot"),
			VOICE_CHAT(0xA5, VoiceChat.class, "VoiceChat"),
			WEB_SHOP(0xA6, WebShop.class, "WebShop"),
			EVENT_SYSTEM(0xA7, EventSystem.class, "EventSystem"),
			BATTLE_PASS(0xA8, BattlePass.class, "BattlePass"),
			DAILY_MISSION(0xA9, DailyMission.class, "DailyMission"),
			ATTENDANCE(0xAA, Attendance.class, "Attendance"),
			GACHA_SYSTEM(0xAB, GachaSystem.class, "GachaSystem"),
			CASH_SHOP(0xAC, CashShop.class, "CashShop"),
			PREMIUM_SERVICE(0xAD, PremiumService.class, "PremiumService"),
			VIP_SYSTEM(0xAE, VipSystem.class, "VipSystem"),
			STREAMING_INTEGRATION(0xAF, StreamingIntegration.class, "StreamingIntegration"),
			CROSS_SERVER(0xB0, CrossServer.class, "CrossServer"),
			DIMENSIONAL_RIFT(0xB1, DimensionalRift.class, "DimensionalRift"),
			MENTORING(0xB2, Mentoring.class, "Mentoring"),
			VITALITY_SYSTEM(0xB3, VitalitySystem.class, "VitalitySystem"),
			AUTOMATED_FARM(0xB4, AutomatedFarm.class, "AutomatedFarm"),
			SMART_HUD(0xB5, SmartHud.class, "SmartHud"),
			AI_COMPANION(0xB6, AiCompanion.class, "AiCompanion"),
			SEASONAL_EVENTS(0xB7, SeasonalEvents.class, "SeasonalEvents"),
			COLLECTION_SYSTEM(0xB8, CollectionSystem.class, "CollectionSystem"),
			TITLE_SYSTEM(0xB9, TitleSystem.class, "TitleSystem"),
			EMOTE_WHEEL(0xBA, EmoteWheel.class, "EmoteWheel"),
			CUSTOMIZABLE_UI(0xBB, CustomizableUi.class, "CustomizableUi"),
			ADVANCED_STATS(0xBC, AdvancedStats.class, "AdvancedStats"),
			REAL_TIME_PVP(0xBD, RealTimePvp.class, "RealTimePvp"),
			GUILD_WAR_SYSTEM(0xBE, GuildWarSystem.class, "GuildWarSystem"),
			RAID_MANAGEMENT(0xBF, RaidManagement.class, "RaidManagement"),
			
			// Network and Sync
			NETWORK_UPDATE(0xC0, NetworkUpdate.class, "NetworkUpdate"),
			POSITION_SYNC(0xC1, PositionSync.class, "PositionSync"),
			STATE_SYNC(0xC2, StateSync.class, "StateSync"),
			INVENTORY_SYNC(0xC3, InventorySync.class, "InventorySync"),
			SKILL_SYNC(0xC4, SkillSync.class, "SkillSync"),
			QUEST_SYNC(0xC5, QuestSync.class, "QuestSync"),
			CLAN_SYNC(0xC6, ClanSync.class, "ClanSync"),
			PARTY_SYNC(0xC7, PartySync.class, "PartySync"),
			FRIEND_SYNC(0xC8, FriendSync.class, "FriendSync"),
			MAIL_SYNC(0xC9, MailSync.class, "MailSync"),
			WAREHOUSE_SYNC(0xCA, WarehouseSync.class, "WarehouseSync"),
			SHORTCUT_SYNC(0xCB, ShortcutSync.class, "ShortcutSync"),
			MACRO_SYNC(0xCC, MacroSync.class, "MacroSync"),
			SETTING_SYNC(0xCD, SettingSync.class, "SettingSync"),
			APPEARANCE_SYNC(0xCE, AppearanceSync.class, "AppearanceSync"),
			MOUNT_SYNC(0xCF, MountSync.class, "MountSync"),
			PET_SYNC(0xD0, PetSync.class, "PetSync"),
			SUMMON_SYNC(0xD1, SummonSync.class, "SummonSync"),
			CUBIC_SYNC(0xD2, CubicSync.class, "CubicSync"),
			AGATHION_SYNC(0xD3, AgathionSync.class, "AgathionSync"),
			TRANSFORMATION_SYNC(0xD4, TransformationSync.class, "TransformationSync"),
			AUGMENTATION_SYNC(0xD5, AugmentationSync.class, "AugmentationSync"),
			ELEMENTAL_SYNC(0xD6, ElementalSync.class, "ElementalSync"),
			ACHIEVEMENT_SYNC(0xD7, AchievementSync.class, "AchievementSync"),
			TITLE_SYNC(0xD8, TitleSync.class, "TitleSync"),
			COLLECTION_SYNC(0xD9, CollectionSync.class, "CollectionSync"),
			VITALITY_SYNC(0xDA, VitalitySync.class, "VitalitySync"),
			MENTORING_SYNC(0xDB, MentoringSync.class, "MentoringSync"),
			SEASON_SYNC(0xDC, SeasonSync.class, "SeasonSync"),
			BATTLEPASS_SYNC(0xDD, BattlepassSync.class, "BattlepassSync"),
			EVENT_SYNC(0xDE, EventSync.class, "EventSync"),
			REAL_TIME_DATA_SYNC(0xDF, RealTimeDataSync.class, "RealTimeDataSync"),
			
			// Security and Anti Cheat
			SECURITY_CHECK(0xE0, SecurityCheck.class, "SecurityCheck"),
			INTEGRITY_VERIFY(0xE1, IntegrityVerify.class, "IntegrityVerify"),
			MEMORY_SCAN(0xE2, MemoryScan.class, "MemoryScan"),
			PROCESS_DETECTION(0xE3, ProcessDetection.class, "ProcessDetection"),
			SPEED_HACK_DETECT(0xE4, SpeedHackDetect.class, "SpeedHackDetect"),
			TELEPORT_HACK_DETECT(0xE5, TeleportHackDetect.class, "TeleportHackDetect"),
			WALL_HACK_DETECT(0xE6, WallHackDetect.class, "WallHackDetect"),
			AIMBOT_DETECT(0xE7, AimbotDetect.class, "AimbotDetect"),
			PACKET_INJECTION_DETECT(0xE8, PacketInjectionDetect.class, "PacketInjectionDetect"),
			BOT_PATTERN_DETECT(0xE9, BotPatternDetect.class, "BotPatternDetect"),
			MAC_ADDRESS_CHECK(0xEA, MacAddressCheck.class, "MacAddressCheck"),
			HWID_VALIDATION(0xEB, HwidValidation.class, "HwidValidation"),
			ENCRYPTION_HANDSHAKE(0xEC, EncryptionHandshake.class, "EncryptionHandshake"),
			SESSION_VALIDATION(0xED, SessionValidation.class, "SessionValidation"),
			PACKET_VALIDATION(0xEE, PacketValidation.class, "PacketValidation"),
			CHEAT_REPORT(0xEF, CheatReport.class, "CheatReport"),
			
			// Others
			SYSTEM_LOG(0xF0, SystemLog.class, "SystemLog"),
			PERFORMANCE_MONITOR(0xF1, PerformanceMonitor.class, "PerformanceMonitor"),
			MEMORY_MANAGEMENT(0xF2, MemoryManagement.class, "MemoryManagement"),
			THREAD_SYNC(0xF3, ThreadSync.class, "ThreadSync"),
			DATABASE_QUERY(0xF4, DatabaseQuery.class, "DatabaseQuery"),
			CACHE_MANAGEMENT(0xF5, CacheManagement.class, "CacheManagement"),
			CONFIGURATION_MANAGE(0xF6, ConfigurationManage.class, "ConfigurationManage"),
			PATCH_SYSTEM(0xF7, PatchSystem.class, "PatchSystem"),
			UPDATE_CHECK(0xF8, UpdateCheck.class, "UpdateCheck"),
			ERROR_REPORTING(0xF9, ErrorReporting.class, "ErrorReporting"),
			DEBUG_TRACE(0xFA, DebugTrace.class, "DebugTrace"),
			PROFILING_DATA(0xFB, ProfilingData.class, "ProfilingData"),
			METRICS_COLLECTION(0xFC, MetricsCollection.class, "MetricsCollection"),
			BACKUP_SYSTEM(0xFD, BackupSystem.class, "BackupSystem"),
			RECOVERY_MODE(0xFE, RecoveryMode.class, "RecoveryMode"),
			RESERVED_SYSTEM(0xFF, ReservedSystem.class, "ReservedSystem");
		   */
        
        private final int opcode;
        private final Class<? extends Token> bytecode;
        private final String name;

        Main(int opcode, Class<? extends Token> mainclass, String name) {
            this.opcode = opcode;
            this.bytecode = mainclass;
            this.name = name;
        }

        public int getOpcode() { return opcode; }
        public Class<? extends Token> getBytecode() { return bytecode; }
        public String getName() { return name; }

        private static final Map<Integer, Main> LOOKUP = new HashMap<>();
        static {
            for (Main op : values()) LOOKUP.put(op.opcode, op);
        }
        public static Main fromInt(int op) { return LOOKUP.get(op); }
    }
}