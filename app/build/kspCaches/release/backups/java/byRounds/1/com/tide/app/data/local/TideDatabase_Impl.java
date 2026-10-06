package com.tide.app.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.tide.app.data.local.dao.AddictionDao;
import com.tide.app.data.local.dao.AddictionDao_Impl;
import com.tide.app.data.local.dao.ConsumptionEntryDao;
import com.tide.app.data.local.dao.ConsumptionEntryDao_Impl;
import com.tide.app.data.local.dao.CravingEntryDao;
import com.tide.app.data.local.dao.CravingEntryDao_Impl;
import com.tide.app.data.local.dao.DailyCheckInDao;
import com.tide.app.data.local.dao.DailyCheckInDao_Impl;
import com.tide.app.data.local.dao.GoalDao;
import com.tide.app.data.local.dao.GoalDao_Impl;
import com.tide.app.data.local.dao.MilestoneDao;
import com.tide.app.data.local.dao.MilestoneDao_Impl;
import com.tide.app.data.local.dao.NotificationPreferenceDao;
import com.tide.app.data.local.dao.NotificationPreferenceDao_Impl;
import com.tide.app.data.local.dao.ProductDao;
import com.tide.app.data.local.dao.ProductDao_Impl;
import com.tide.app.data.local.dao.RecoveryGoalDao;
import com.tide.app.data.local.dao.RecoveryGoalDao_Impl;
import com.tide.app.data.local.dao.SlipEntryDao;
import com.tide.app.data.local.dao.SlipEntryDao_Impl;
import com.tide.app.data.local.dao.TriggerDao;
import com.tide.app.data.local.dao.TriggerDao_Impl;
import com.tide.app.data.local.dao.UserSettingsDao;
import com.tide.app.data.local.dao.UserSettingsDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class TideDatabase_Impl extends TideDatabase {
  private volatile AddictionDao _addictionDao;

  private volatile ProductDao _productDao;

  private volatile ConsumptionEntryDao _consumptionEntryDao;

  private volatile CravingEntryDao _cravingEntryDao;

  private volatile SlipEntryDao _slipEntryDao;

  private volatile DailyCheckInDao _dailyCheckInDao;

  private volatile TriggerDao _triggerDao;

  private volatile GoalDao _goalDao;

  private volatile RecoveryGoalDao _recoveryGoalDao;

  private volatile MilestoneDao _milestoneDao;

  private volatile NotificationPreferenceDao _notificationPreferenceDao;

  private volatile UserSettingsDao _userSettingsDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `addictions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `category` TEXT NOT NULL, `unit` TEXT NOT NULL, `customUnit` TEXT, `baselineAmount` REAL NOT NULL, `baselineFrequency` TEXT NOT NULL, `baselineCost` REAL NOT NULL, `currency` TEXT NOT NULL, `goalType` TEXT NOT NULL, `startDate` INTEGER NOT NULL, `active` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `products` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `addictionId` INTEGER NOT NULL, `brand` TEXT NOT NULL, `productName` TEXT NOT NULL, `unit` TEXT NOT NULL, `price` REAL NOT NULL, `pricingBasis` TEXT NOT NULL, `unitsPerBasis` REAL NOT NULL, `unitCost` REAL NOT NULL, FOREIGN KEY(`addictionId`) REFERENCES `addictions`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_products_addictionId` ON `products` (`addictionId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `consumption_entries` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `addictionId` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL, `amount` REAL NOT NULL, `calculatedCost` REAL NOT NULL, `triggerId` INTEGER, `note` TEXT, FOREIGN KEY(`addictionId`) REFERENCES `addictions`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_consumption_entries_addictionId` ON `consumption_entries` (`addictionId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_consumption_entries_timestamp` ON `consumption_entries` (`timestamp`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `craving_entries` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `addictionId` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL, `intensity` INTEGER NOT NULL, `triggerId` INTEGER, `mood` TEXT, `resisted` INTEGER NOT NULL, `note` TEXT, FOREIGN KEY(`addictionId`) REFERENCES `addictions`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_craving_entries_addictionId` ON `craving_entries` (`addictionId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_craving_entries_timestamp` ON `craving_entries` (`timestamp`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `slip_entries` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `addictionId` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL, `amount` REAL NOT NULL, `cost` REAL NOT NULL, `triggerId` INTEGER, `mood` TEXT, `note` TEXT, FOREIGN KEY(`addictionId`) REFERENCES `addictions`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_slip_entries_addictionId` ON `slip_entries` (`addictionId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_slip_entries_timestamp` ON `slip_entries` (`timestamp`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `daily_check_ins` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `addictionId` INTEGER NOT NULL, `date` TEXT NOT NULL, `mood` TEXT NOT NULL, `cravingOccurred` INTEGER NOT NULL, `cravingIntensity` INTEGER NOT NULL, `note` TEXT, FOREIGN KEY(`addictionId`) REFERENCES `addictions`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_daily_check_ins_addictionId` ON `daily_check_ins` (`addictionId`)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_daily_check_ins_addictionId_date` ON `daily_check_ins` (`addictionId`, `date`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `triggers` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `category` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `goals` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `addictionId` INTEGER NOT NULL, `type` TEXT NOT NULL, `targetAmount` REAL NOT NULL, `targetFrequency` TEXT NOT NULL, `targetDate` INTEGER, `active` INTEGER NOT NULL, FOREIGN KEY(`addictionId`) REFERENCES `addictions`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_goals_addictionId` ON `goals` (`addictionId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `recovery_goals` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `targetAmount` REAL NOT NULL, `currentAmount` REAL NOT NULL, `achieved` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `milestones` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `type` TEXT NOT NULL, `title` TEXT NOT NULL, `description` TEXT NOT NULL, `targetValue` REAL NOT NULL, `achievedAt` INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `notification_preferences` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `type` TEXT NOT NULL, `enabled` INTEGER NOT NULL, `time` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `user_settings` (`id` INTEGER NOT NULL, `themeMode` TEXT NOT NULL, `hapticsEnabled` INTEGER NOT NULL, `soundEnabled` INTEGER NOT NULL, `onboardingCompleted` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'a882bd6299fbee95e0e9d983f89b8220')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `addictions`");
        db.execSQL("DROP TABLE IF EXISTS `products`");
        db.execSQL("DROP TABLE IF EXISTS `consumption_entries`");
        db.execSQL("DROP TABLE IF EXISTS `craving_entries`");
        db.execSQL("DROP TABLE IF EXISTS `slip_entries`");
        db.execSQL("DROP TABLE IF EXISTS `daily_check_ins`");
        db.execSQL("DROP TABLE IF EXISTS `triggers`");
        db.execSQL("DROP TABLE IF EXISTS `goals`");
        db.execSQL("DROP TABLE IF EXISTS `recovery_goals`");
        db.execSQL("DROP TABLE IF EXISTS `milestones`");
        db.execSQL("DROP TABLE IF EXISTS `notification_preferences`");
        db.execSQL("DROP TABLE IF EXISTS `user_settings`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsAddictions = new HashMap<String, TableInfo.Column>(12);
        _columnsAddictions.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAddictions.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAddictions.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAddictions.put("unit", new TableInfo.Column("unit", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAddictions.put("customUnit", new TableInfo.Column("customUnit", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAddictions.put("baselineAmount", new TableInfo.Column("baselineAmount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAddictions.put("baselineFrequency", new TableInfo.Column("baselineFrequency", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAddictions.put("baselineCost", new TableInfo.Column("baselineCost", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAddictions.put("currency", new TableInfo.Column("currency", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAddictions.put("goalType", new TableInfo.Column("goalType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAddictions.put("startDate", new TableInfo.Column("startDate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAddictions.put("active", new TableInfo.Column("active", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAddictions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAddictions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoAddictions = new TableInfo("addictions", _columnsAddictions, _foreignKeysAddictions, _indicesAddictions);
        final TableInfo _existingAddictions = TableInfo.read(db, "addictions");
        if (!_infoAddictions.equals(_existingAddictions)) {
          return new RoomOpenHelper.ValidationResult(false, "addictions(com.tide.app.data.local.entity.AddictionEntity).\n"
                  + " Expected:\n" + _infoAddictions + "\n"
                  + " Found:\n" + _existingAddictions);
        }
        final HashMap<String, TableInfo.Column> _columnsProducts = new HashMap<String, TableInfo.Column>(9);
        _columnsProducts.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("addictionId", new TableInfo.Column("addictionId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("brand", new TableInfo.Column("brand", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("productName", new TableInfo.Column("productName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("unit", new TableInfo.Column("unit", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("price", new TableInfo.Column("price", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("pricingBasis", new TableInfo.Column("pricingBasis", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("unitsPerBasis", new TableInfo.Column("unitsPerBasis", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("unitCost", new TableInfo.Column("unitCost", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysProducts = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysProducts.add(new TableInfo.ForeignKey("addictions", "CASCADE", "NO ACTION", Arrays.asList("addictionId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesProducts = new HashSet<TableInfo.Index>(1);
        _indicesProducts.add(new TableInfo.Index("index_products_addictionId", false, Arrays.asList("addictionId"), Arrays.asList("ASC")));
        final TableInfo _infoProducts = new TableInfo("products", _columnsProducts, _foreignKeysProducts, _indicesProducts);
        final TableInfo _existingProducts = TableInfo.read(db, "products");
        if (!_infoProducts.equals(_existingProducts)) {
          return new RoomOpenHelper.ValidationResult(false, "products(com.tide.app.data.local.entity.ProductEntity).\n"
                  + " Expected:\n" + _infoProducts + "\n"
                  + " Found:\n" + _existingProducts);
        }
        final HashMap<String, TableInfo.Column> _columnsConsumptionEntries = new HashMap<String, TableInfo.Column>(7);
        _columnsConsumptionEntries.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsConsumptionEntries.put("addictionId", new TableInfo.Column("addictionId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsConsumptionEntries.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsConsumptionEntries.put("amount", new TableInfo.Column("amount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsConsumptionEntries.put("calculatedCost", new TableInfo.Column("calculatedCost", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsConsumptionEntries.put("triggerId", new TableInfo.Column("triggerId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsConsumptionEntries.put("note", new TableInfo.Column("note", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysConsumptionEntries = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysConsumptionEntries.add(new TableInfo.ForeignKey("addictions", "CASCADE", "NO ACTION", Arrays.asList("addictionId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesConsumptionEntries = new HashSet<TableInfo.Index>(2);
        _indicesConsumptionEntries.add(new TableInfo.Index("index_consumption_entries_addictionId", false, Arrays.asList("addictionId"), Arrays.asList("ASC")));
        _indicesConsumptionEntries.add(new TableInfo.Index("index_consumption_entries_timestamp", false, Arrays.asList("timestamp"), Arrays.asList("ASC")));
        final TableInfo _infoConsumptionEntries = new TableInfo("consumption_entries", _columnsConsumptionEntries, _foreignKeysConsumptionEntries, _indicesConsumptionEntries);
        final TableInfo _existingConsumptionEntries = TableInfo.read(db, "consumption_entries");
        if (!_infoConsumptionEntries.equals(_existingConsumptionEntries)) {
          return new RoomOpenHelper.ValidationResult(false, "consumption_entries(com.tide.app.data.local.entity.ConsumptionEntryEntity).\n"
                  + " Expected:\n" + _infoConsumptionEntries + "\n"
                  + " Found:\n" + _existingConsumptionEntries);
        }
        final HashMap<String, TableInfo.Column> _columnsCravingEntries = new HashMap<String, TableInfo.Column>(8);
        _columnsCravingEntries.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCravingEntries.put("addictionId", new TableInfo.Column("addictionId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCravingEntries.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCravingEntries.put("intensity", new TableInfo.Column("intensity", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCravingEntries.put("triggerId", new TableInfo.Column("triggerId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCravingEntries.put("mood", new TableInfo.Column("mood", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCravingEntries.put("resisted", new TableInfo.Column("resisted", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCravingEntries.put("note", new TableInfo.Column("note", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCravingEntries = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysCravingEntries.add(new TableInfo.ForeignKey("addictions", "CASCADE", "NO ACTION", Arrays.asList("addictionId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesCravingEntries = new HashSet<TableInfo.Index>(2);
        _indicesCravingEntries.add(new TableInfo.Index("index_craving_entries_addictionId", false, Arrays.asList("addictionId"), Arrays.asList("ASC")));
        _indicesCravingEntries.add(new TableInfo.Index("index_craving_entries_timestamp", false, Arrays.asList("timestamp"), Arrays.asList("ASC")));
        final TableInfo _infoCravingEntries = new TableInfo("craving_entries", _columnsCravingEntries, _foreignKeysCravingEntries, _indicesCravingEntries);
        final TableInfo _existingCravingEntries = TableInfo.read(db, "craving_entries");
        if (!_infoCravingEntries.equals(_existingCravingEntries)) {
          return new RoomOpenHelper.ValidationResult(false, "craving_entries(com.tide.app.data.local.entity.CravingEntryEntity).\n"
                  + " Expected:\n" + _infoCravingEntries + "\n"
                  + " Found:\n" + _existingCravingEntries);
        }
        final HashMap<String, TableInfo.Column> _columnsSlipEntries = new HashMap<String, TableInfo.Column>(8);
        _columnsSlipEntries.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSlipEntries.put("addictionId", new TableInfo.Column("addictionId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSlipEntries.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSlipEntries.put("amount", new TableInfo.Column("amount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSlipEntries.put("cost", new TableInfo.Column("cost", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSlipEntries.put("triggerId", new TableInfo.Column("triggerId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSlipEntries.put("mood", new TableInfo.Column("mood", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSlipEntries.put("note", new TableInfo.Column("note", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSlipEntries = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysSlipEntries.add(new TableInfo.ForeignKey("addictions", "CASCADE", "NO ACTION", Arrays.asList("addictionId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesSlipEntries = new HashSet<TableInfo.Index>(2);
        _indicesSlipEntries.add(new TableInfo.Index("index_slip_entries_addictionId", false, Arrays.asList("addictionId"), Arrays.asList("ASC")));
        _indicesSlipEntries.add(new TableInfo.Index("index_slip_entries_timestamp", false, Arrays.asList("timestamp"), Arrays.asList("ASC")));
        final TableInfo _infoSlipEntries = new TableInfo("slip_entries", _columnsSlipEntries, _foreignKeysSlipEntries, _indicesSlipEntries);
        final TableInfo _existingSlipEntries = TableInfo.read(db, "slip_entries");
        if (!_infoSlipEntries.equals(_existingSlipEntries)) {
          return new RoomOpenHelper.ValidationResult(false, "slip_entries(com.tide.app.data.local.entity.SlipEntryEntity).\n"
                  + " Expected:\n" + _infoSlipEntries + "\n"
                  + " Found:\n" + _existingSlipEntries);
        }
        final HashMap<String, TableInfo.Column> _columnsDailyCheckIns = new HashMap<String, TableInfo.Column>(7);
        _columnsDailyCheckIns.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyCheckIns.put("addictionId", new TableInfo.Column("addictionId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyCheckIns.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyCheckIns.put("mood", new TableInfo.Column("mood", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyCheckIns.put("cravingOccurred", new TableInfo.Column("cravingOccurred", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyCheckIns.put("cravingIntensity", new TableInfo.Column("cravingIntensity", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyCheckIns.put("note", new TableInfo.Column("note", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDailyCheckIns = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysDailyCheckIns.add(new TableInfo.ForeignKey("addictions", "CASCADE", "NO ACTION", Arrays.asList("addictionId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesDailyCheckIns = new HashSet<TableInfo.Index>(2);
        _indicesDailyCheckIns.add(new TableInfo.Index("index_daily_check_ins_addictionId", false, Arrays.asList("addictionId"), Arrays.asList("ASC")));
        _indicesDailyCheckIns.add(new TableInfo.Index("index_daily_check_ins_addictionId_date", true, Arrays.asList("addictionId", "date"), Arrays.asList("ASC", "ASC")));
        final TableInfo _infoDailyCheckIns = new TableInfo("daily_check_ins", _columnsDailyCheckIns, _foreignKeysDailyCheckIns, _indicesDailyCheckIns);
        final TableInfo _existingDailyCheckIns = TableInfo.read(db, "daily_check_ins");
        if (!_infoDailyCheckIns.equals(_existingDailyCheckIns)) {
          return new RoomOpenHelper.ValidationResult(false, "daily_check_ins(com.tide.app.data.local.entity.DailyCheckInEntity).\n"
                  + " Expected:\n" + _infoDailyCheckIns + "\n"
                  + " Found:\n" + _existingDailyCheckIns);
        }
        final HashMap<String, TableInfo.Column> _columnsTriggers = new HashMap<String, TableInfo.Column>(3);
        _columnsTriggers.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTriggers.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTriggers.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTriggers = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTriggers = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTriggers = new TableInfo("triggers", _columnsTriggers, _foreignKeysTriggers, _indicesTriggers);
        final TableInfo _existingTriggers = TableInfo.read(db, "triggers");
        if (!_infoTriggers.equals(_existingTriggers)) {
          return new RoomOpenHelper.ValidationResult(false, "triggers(com.tide.app.data.local.entity.TriggerEntity).\n"
                  + " Expected:\n" + _infoTriggers + "\n"
                  + " Found:\n" + _existingTriggers);
        }
        final HashMap<String, TableInfo.Column> _columnsGoals = new HashMap<String, TableInfo.Column>(7);
        _columnsGoals.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGoals.put("addictionId", new TableInfo.Column("addictionId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGoals.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGoals.put("targetAmount", new TableInfo.Column("targetAmount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGoals.put("targetFrequency", new TableInfo.Column("targetFrequency", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGoals.put("targetDate", new TableInfo.Column("targetDate", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGoals.put("active", new TableInfo.Column("active", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysGoals = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysGoals.add(new TableInfo.ForeignKey("addictions", "CASCADE", "NO ACTION", Arrays.asList("addictionId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesGoals = new HashSet<TableInfo.Index>(1);
        _indicesGoals.add(new TableInfo.Index("index_goals_addictionId", false, Arrays.asList("addictionId"), Arrays.asList("ASC")));
        final TableInfo _infoGoals = new TableInfo("goals", _columnsGoals, _foreignKeysGoals, _indicesGoals);
        final TableInfo _existingGoals = TableInfo.read(db, "goals");
        if (!_infoGoals.equals(_existingGoals)) {
          return new RoomOpenHelper.ValidationResult(false, "goals(com.tide.app.data.local.entity.GoalEntity).\n"
                  + " Expected:\n" + _infoGoals + "\n"
                  + " Found:\n" + _existingGoals);
        }
        final HashMap<String, TableInfo.Column> _columnsRecoveryGoals = new HashMap<String, TableInfo.Column>(6);
        _columnsRecoveryGoals.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecoveryGoals.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecoveryGoals.put("targetAmount", new TableInfo.Column("targetAmount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecoveryGoals.put("currentAmount", new TableInfo.Column("currentAmount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecoveryGoals.put("achieved", new TableInfo.Column("achieved", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecoveryGoals.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRecoveryGoals = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesRecoveryGoals = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoRecoveryGoals = new TableInfo("recovery_goals", _columnsRecoveryGoals, _foreignKeysRecoveryGoals, _indicesRecoveryGoals);
        final TableInfo _existingRecoveryGoals = TableInfo.read(db, "recovery_goals");
        if (!_infoRecoveryGoals.equals(_existingRecoveryGoals)) {
          return new RoomOpenHelper.ValidationResult(false, "recovery_goals(com.tide.app.data.local.entity.RecoveryGoalEntity).\n"
                  + " Expected:\n" + _infoRecoveryGoals + "\n"
                  + " Found:\n" + _existingRecoveryGoals);
        }
        final HashMap<String, TableInfo.Column> _columnsMilestones = new HashMap<String, TableInfo.Column>(6);
        _columnsMilestones.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMilestones.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMilestones.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMilestones.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMilestones.put("targetValue", new TableInfo.Column("targetValue", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMilestones.put("achievedAt", new TableInfo.Column("achievedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMilestones = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMilestones = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoMilestones = new TableInfo("milestones", _columnsMilestones, _foreignKeysMilestones, _indicesMilestones);
        final TableInfo _existingMilestones = TableInfo.read(db, "milestones");
        if (!_infoMilestones.equals(_existingMilestones)) {
          return new RoomOpenHelper.ValidationResult(false, "milestones(com.tide.app.data.local.entity.MilestoneEntity).\n"
                  + " Expected:\n" + _infoMilestones + "\n"
                  + " Found:\n" + _existingMilestones);
        }
        final HashMap<String, TableInfo.Column> _columnsNotificationPreferences = new HashMap<String, TableInfo.Column>(4);
        _columnsNotificationPreferences.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotificationPreferences.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotificationPreferences.put("enabled", new TableInfo.Column("enabled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotificationPreferences.put("time", new TableInfo.Column("time", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysNotificationPreferences = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesNotificationPreferences = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoNotificationPreferences = new TableInfo("notification_preferences", _columnsNotificationPreferences, _foreignKeysNotificationPreferences, _indicesNotificationPreferences);
        final TableInfo _existingNotificationPreferences = TableInfo.read(db, "notification_preferences");
        if (!_infoNotificationPreferences.equals(_existingNotificationPreferences)) {
          return new RoomOpenHelper.ValidationResult(false, "notification_preferences(com.tide.app.data.local.entity.NotificationPreferenceEntity).\n"
                  + " Expected:\n" + _infoNotificationPreferences + "\n"
                  + " Found:\n" + _existingNotificationPreferences);
        }
        final HashMap<String, TableInfo.Column> _columnsUserSettings = new HashMap<String, TableInfo.Column>(5);
        _columnsUserSettings.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserSettings.put("themeMode", new TableInfo.Column("themeMode", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserSettings.put("hapticsEnabled", new TableInfo.Column("hapticsEnabled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserSettings.put("soundEnabled", new TableInfo.Column("soundEnabled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserSettings.put("onboardingCompleted", new TableInfo.Column("onboardingCompleted", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUserSettings = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUserSettings = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUserSettings = new TableInfo("user_settings", _columnsUserSettings, _foreignKeysUserSettings, _indicesUserSettings);
        final TableInfo _existingUserSettings = TableInfo.read(db, "user_settings");
        if (!_infoUserSettings.equals(_existingUserSettings)) {
          return new RoomOpenHelper.ValidationResult(false, "user_settings(com.tide.app.data.local.entity.UserSettingsEntity).\n"
                  + " Expected:\n" + _infoUserSettings + "\n"
                  + " Found:\n" + _existingUserSettings);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "a882bd6299fbee95e0e9d983f89b8220", "9c52522e70731f1021549571ebe53289");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "addictions","products","consumption_entries","craving_entries","slip_entries","daily_check_ins","triggers","goals","recovery_goals","milestones","notification_preferences","user_settings");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `addictions`");
      _db.execSQL("DELETE FROM `products`");
      _db.execSQL("DELETE FROM `consumption_entries`");
      _db.execSQL("DELETE FROM `craving_entries`");
      _db.execSQL("DELETE FROM `slip_entries`");
      _db.execSQL("DELETE FROM `daily_check_ins`");
      _db.execSQL("DELETE FROM `triggers`");
      _db.execSQL("DELETE FROM `goals`");
      _db.execSQL("DELETE FROM `recovery_goals`");
      _db.execSQL("DELETE FROM `milestones`");
      _db.execSQL("DELETE FROM `notification_preferences`");
      _db.execSQL("DELETE FROM `user_settings`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(AddictionDao.class, AddictionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ProductDao.class, ProductDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ConsumptionEntryDao.class, ConsumptionEntryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(CravingEntryDao.class, CravingEntryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SlipEntryDao.class, SlipEntryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(DailyCheckInDao.class, DailyCheckInDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(TriggerDao.class, TriggerDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(GoalDao.class, GoalDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(RecoveryGoalDao.class, RecoveryGoalDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(MilestoneDao.class, MilestoneDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(NotificationPreferenceDao.class, NotificationPreferenceDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(UserSettingsDao.class, UserSettingsDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public AddictionDao addictionDao() {
    if (_addictionDao != null) {
      return _addictionDao;
    } else {
      synchronized(this) {
        if(_addictionDao == null) {
          _addictionDao = new AddictionDao_Impl(this);
        }
        return _addictionDao;
      }
    }
  }

  @Override
  public ProductDao productDao() {
    if (_productDao != null) {
      return _productDao;
    } else {
      synchronized(this) {
        if(_productDao == null) {
          _productDao = new ProductDao_Impl(this);
        }
        return _productDao;
      }
    }
  }

  @Override
  public ConsumptionEntryDao consumptionEntryDao() {
    if (_consumptionEntryDao != null) {
      return _consumptionEntryDao;
    } else {
      synchronized(this) {
        if(_consumptionEntryDao == null) {
          _consumptionEntryDao = new ConsumptionEntryDao_Impl(this);
        }
        return _consumptionEntryDao;
      }
    }
  }

  @Override
  public CravingEntryDao cravingEntryDao() {
    if (_cravingEntryDao != null) {
      return _cravingEntryDao;
    } else {
      synchronized(this) {
        if(_cravingEntryDao == null) {
          _cravingEntryDao = new CravingEntryDao_Impl(this);
        }
        return _cravingEntryDao;
      }
    }
  }

  @Override
  public SlipEntryDao slipEntryDao() {
    if (_slipEntryDao != null) {
      return _slipEntryDao;
    } else {
      synchronized(this) {
        if(_slipEntryDao == null) {
          _slipEntryDao = new SlipEntryDao_Impl(this);
        }
        return _slipEntryDao;
      }
    }
  }

  @Override
  public DailyCheckInDao dailyCheckInDao() {
    if (_dailyCheckInDao != null) {
      return _dailyCheckInDao;
    } else {
      synchronized(this) {
        if(_dailyCheckInDao == null) {
          _dailyCheckInDao = new DailyCheckInDao_Impl(this);
        }
        return _dailyCheckInDao;
      }
    }
  }

  @Override
  public TriggerDao triggerDao() {
    if (_triggerDao != null) {
      return _triggerDao;
    } else {
      synchronized(this) {
        if(_triggerDao == null) {
          _triggerDao = new TriggerDao_Impl(this);
        }
        return _triggerDao;
      }
    }
  }

  @Override
  public GoalDao goalDao() {
    if (_goalDao != null) {
      return _goalDao;
    } else {
      synchronized(this) {
        if(_goalDao == null) {
          _goalDao = new GoalDao_Impl(this);
        }
        return _goalDao;
      }
    }
  }

  @Override
  public RecoveryGoalDao recoveryGoalDao() {
    if (_recoveryGoalDao != null) {
      return _recoveryGoalDao;
    } else {
      synchronized(this) {
        if(_recoveryGoalDao == null) {
          _recoveryGoalDao = new RecoveryGoalDao_Impl(this);
        }
        return _recoveryGoalDao;
      }
    }
  }

  @Override
  public MilestoneDao milestoneDao() {
    if (_milestoneDao != null) {
      return _milestoneDao;
    } else {
      synchronized(this) {
        if(_milestoneDao == null) {
          _milestoneDao = new MilestoneDao_Impl(this);
        }
        return _milestoneDao;
      }
    }
  }

  @Override
  public NotificationPreferenceDao notificationPreferenceDao() {
    if (_notificationPreferenceDao != null) {
      return _notificationPreferenceDao;
    } else {
      synchronized(this) {
        if(_notificationPreferenceDao == null) {
          _notificationPreferenceDao = new NotificationPreferenceDao_Impl(this);
        }
        return _notificationPreferenceDao;
      }
    }
  }

  @Override
  public UserSettingsDao userSettingsDao() {
    if (_userSettingsDao != null) {
      return _userSettingsDao;
    } else {
      synchronized(this) {
        if(_userSettingsDao == null) {
          _userSettingsDao = new UserSettingsDao_Impl(this);
        }
        return _userSettingsDao;
      }
    }
  }
}
