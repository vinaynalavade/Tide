package com.tide.app.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.tide.app.data.local.entity.GoalEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class GoalDao_Impl implements GoalDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<GoalEntity> __insertionAdapterOfGoalEntity;

  private final EntityDeletionOrUpdateAdapter<GoalEntity> __updateAdapterOfGoalEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public GoalDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfGoalEntity = new EntityInsertionAdapter<GoalEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `goals` (`id`,`addictionId`,`type`,`targetAmount`,`targetFrequency`,`targetDate`,`active`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final GoalEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getAddictionId());
        statement.bindString(3, entity.getType());
        statement.bindDouble(4, entity.getTargetAmount());
        statement.bindString(5, entity.getTargetFrequency());
        if (entity.getTargetDate() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getTargetDate());
        }
        final int _tmp = entity.getActive() ? 1 : 0;
        statement.bindLong(7, _tmp);
      }
    };
    this.__updateAdapterOfGoalEntity = new EntityDeletionOrUpdateAdapter<GoalEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `goals` SET `id` = ?,`addictionId` = ?,`type` = ?,`targetAmount` = ?,`targetFrequency` = ?,`targetDate` = ?,`active` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final GoalEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getAddictionId());
        statement.bindString(3, entity.getType());
        statement.bindDouble(4, entity.getTargetAmount());
        statement.bindString(5, entity.getTargetFrequency());
        if (entity.getTargetDate() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getTargetDate());
        }
        final int _tmp = entity.getActive() ? 1 : 0;
        statement.bindLong(7, _tmp);
        statement.bindLong(8, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM goals";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final GoalEntity goal, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfGoalEntity.insertAndReturnId(goal);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final GoalEntity goal, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfGoalEntity.handle(goal);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<GoalEntity> getActiveGoal(final long addictionId) {
    final String _sql = "SELECT * FROM goals WHERE addictionId = ? AND active = 1 ORDER BY id DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, addictionId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"goals"}, new Callable<GoalEntity>() {
      @Override
      @Nullable
      public GoalEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAddictionId = CursorUtil.getColumnIndexOrThrow(_cursor, "addictionId");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfTargetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "targetAmount");
          final int _cursorIndexOfTargetFrequency = CursorUtil.getColumnIndexOrThrow(_cursor, "targetFrequency");
          final int _cursorIndexOfTargetDate = CursorUtil.getColumnIndexOrThrow(_cursor, "targetDate");
          final int _cursorIndexOfActive = CursorUtil.getColumnIndexOrThrow(_cursor, "active");
          final GoalEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAddictionId;
            _tmpAddictionId = _cursor.getLong(_cursorIndexOfAddictionId);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final double _tmpTargetAmount;
            _tmpTargetAmount = _cursor.getDouble(_cursorIndexOfTargetAmount);
            final String _tmpTargetFrequency;
            _tmpTargetFrequency = _cursor.getString(_cursorIndexOfTargetFrequency);
            final Long _tmpTargetDate;
            if (_cursor.isNull(_cursorIndexOfTargetDate)) {
              _tmpTargetDate = null;
            } else {
              _tmpTargetDate = _cursor.getLong(_cursorIndexOfTargetDate);
            }
            final boolean _tmpActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfActive);
            _tmpActive = _tmp != 0;
            _result = new GoalEntity(_tmpId,_tmpAddictionId,_tmpType,_tmpTargetAmount,_tmpTargetFrequency,_tmpTargetDate,_tmpActive);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getActiveGoalSync(final long addictionId,
      final Continuation<? super GoalEntity> $completion) {
    final String _sql = "SELECT * FROM goals WHERE addictionId = ? AND active = 1 ORDER BY id DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, addictionId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<GoalEntity>() {
      @Override
      @Nullable
      public GoalEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAddictionId = CursorUtil.getColumnIndexOrThrow(_cursor, "addictionId");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfTargetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "targetAmount");
          final int _cursorIndexOfTargetFrequency = CursorUtil.getColumnIndexOrThrow(_cursor, "targetFrequency");
          final int _cursorIndexOfTargetDate = CursorUtil.getColumnIndexOrThrow(_cursor, "targetDate");
          final int _cursorIndexOfActive = CursorUtil.getColumnIndexOrThrow(_cursor, "active");
          final GoalEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAddictionId;
            _tmpAddictionId = _cursor.getLong(_cursorIndexOfAddictionId);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final double _tmpTargetAmount;
            _tmpTargetAmount = _cursor.getDouble(_cursorIndexOfTargetAmount);
            final String _tmpTargetFrequency;
            _tmpTargetFrequency = _cursor.getString(_cursorIndexOfTargetFrequency);
            final Long _tmpTargetDate;
            if (_cursor.isNull(_cursorIndexOfTargetDate)) {
              _tmpTargetDate = null;
            } else {
              _tmpTargetDate = _cursor.getLong(_cursorIndexOfTargetDate);
            }
            final boolean _tmpActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfActive);
            _tmpActive = _tmp != 0;
            _result = new GoalEntity(_tmpId,_tmpAddictionId,_tmpType,_tmpTargetAmount,_tmpTargetFrequency,_tmpTargetDate,_tmpActive);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
