import 'package:path_provider/path_provider.dart';
import 'dart:io';
import 'UserInfo.dart';
import 'dart:convert';
import 'dart:async';

/// This file contains different wrappers to read and write in the application local files.
/// It is mostly used by the background tasks that can't keep volatile state.

const _STARTED_KEY = "started";
const _OVER_KEY = "over";

/// Getter for the path of the application local directory
Future<String> get _localPath async {
  final directory = await getApplicationDocumentsDirectory();
  return directory.path;
}

/// Getter for the file that contains the completed journeys
Future<File> get _journeyData async {
  final path = await _localPath;
  return File('$path/data.json');
}

/// Getter for the file that contains the last geofence id
/// TODO should be deleted soon
Future<File> get _geofenceFile async {
  final path = await _localPath;
  return File('$path/geofence');
}

/// Getter for the file that contains the last point of the latest journey
/// A new journey will start if the user goes out of the circle centered here
Future<File> get _geofenceCenter async {
  final path = await _localPath;
  return File('$path/geofenceCenter');
}

/// Getter for the file that contains information about whether
/// a journey is started or not
Future<File> get _journey async {
  final path = await _localPath;
  return File('$path/journey');
}

/// Getter for the file that contains information about whether
/// the location listener is currently active or not
Future<File> get _llStarted async {
  final path = await _localPath;
  return File('$path/llStarted');
}

/// Getter for the file that contains buffered points, i.e.
/// points that have been measured but that do not define a complete journey
Future<File> get _bufferedPoints async {
  final path = await _localPath;
  return File('$path/points');
}

/// Write 'data' in the file 'where'
_writeInFile(String data, File where) async {
  RandomAccessFile file = await where.open(mode: FileMode.append);
  file.lockSync(FileLock.blockingExclusive);
  await file.writeString(data);
  file.unlockSync();
  file.close();
}

/// Delete buffered journeys
clearFile() async {
  await _clearFile(await _journeyData);
}

/// Delete stored geofence id
clearGeofence() async {
  await _clearFile(await _geofenceFile);
}

/// Delete buffered points
clearBuffer() async {
  await _clearFile(await _bufferedPoints);
}

/// Delete all the content of the file 'which'
_clearFile(File which) async {
  RandomAccessFile file = await which.open(mode: FileMode.write);
  file.lockSync(FileLock.blockingExclusive);
  await file.truncate(0);
  file.unlockSync();
  file.close();
}

/// Returns buffered journeys. In case of exception, an empty string is returned
/// (as if the file was really empty)
Future<String> readFile() async {
  return _readFile(await _journeyData);
}

/// Returns the content of the file 'which'
Future<String> _readFile(File which) async {
  try {
    return await which.readAsString();
  } catch (e) {
    // return empty file
    return "";
  }
}

/// Store the id given in argument. The previous id is replaced if existing
storeGeofenceById(String id) async {
  File file = await _geofenceFile;
  //TODO for defect table : forgotten await so file became empty for no reason
  await _clearFile(file);
  await _writeInFile(id, file);
}

/// Store the given point (time and position), it can be retrieved later
/// with getLastTimedPos or used to create a journey with
/// writeJourneyFromBufferedPoints. A new journey is automatically started
/// The calendar should be formatted as "yyyy-MM-dd HH-mm-ss"
storePoint(String calendar, String lat, String lon) async {
  await _writeInFile(calendar + "," + lat + "," + lon + "\n", await _bufferedPoints);
  await _startJourney();
}

/// Store the given point as a geofence center
storeGeofenceCenter(String calendar, String latitude, String longitude) async {
  await _writeInFile(calendar + "," + latitude + "," + longitude, await _geofenceCenter);
}

/// Retrieve the previously stored geofence id
Future<String> getLastGeofenceId() async {
  String id = await _readFile(await _geofenceFile);
  if (id == "") {
    return null;
  }
  return id;
}

/// Retrieve the latest stored point, or the geofence center if
/// no journey has started yet
Future<List<String>> getLastTimedLoc() async {
  if (!(await hasJourneyStarted())) {
    String data = await _readFile(await _geofenceCenter);
    if (data == "") {
      return null;
    }
    // sublist because the split method returns a last empty string
    return data.split(",").sublist(0, 3);
  }
  String data = await _readFile(await _bufferedPoints);
  if (data == "") {
    return null;
  }
  List<String> points = data.split("\n");
  // -2 and sublist because the split method returns a last empty string
  return points[points.length - 2].split(",").sublist(0, 3);
}

/// Retrieve the latest stored calendar
/// TODO should be deleted soon
Future<String> getLastCalendar() async {
  String data = await _readFile(await _bufferedPoints);
  if (data == "") {
    return null;
  }
  List<String> points = data.split("\n");
  /// -2 because the split method returns a last empty string
  return points[points.length - 2].split(",")[0];
}

/// Create and store a journey from the currently buffered points,
/// end the active journey and erase the buffered points
writeJourneyFromBufferedPoints(UserInfo user) async {
  List<String> points = (await _readFile(await _bufferedPoints)).split("\n");
  /// Once again, the split method return a last empty string
  points = points.sublist(0, points.length - 1);
  List<String> calendarAndPos;
  for (String point in points) {
    calendarAndPos = point.split(",");
    user.addData(calendarAndPos[0], calendarAndPos[1], calendarAndPos[2]);
  }
  String jSon = json.encode(user);
  await _writeInFile(jSon + "data_splitter", await _journeyData);
  user.clear();
  await _clearFile(await _bufferedPoints);
  await _endJourney();
}

/// Store a key telling that a journey is currently active
_startJourney() async {
  File file = await _journey;
  await _clearFile(file);
  await _writeInFile(_STARTED_KEY, file);
}

/// Store a key telling that no journey is currently active
_endJourney() async {
  File file = await _journey;
  await _clearFile(file);
  await _writeInFile(_OVER_KEY, file);
}

/// Whether a journey is currently active or not
Future<bool> hasJourneyStarted() async {
  String data = await _readFile(await _journey);
  if (data == "" || data == _OVER_KEY) {
    return false;
  }
  return true;
}

/// Store a key telling that the location listener has been started
startedLocListener() async {
  File file = await _llStarted;
  await _clearFile(file);
  await _writeInFile(_STARTED_KEY, file);
}

/// Store a key telling that the location listener has been stopped
stoppedLocListener() async {
  File file = await _llStarted;
  await _clearFile(file);
  await _writeInFile(_OVER_KEY, file);
}

/// Whether the last action applied to the location listener
/// was a start or a stop
Future<bool> isLocListenerStarted() async {
  String data = await _readFile(await _llStarted);
  if (data == "" || data == _OVER_KEY) {
    return false;
  }
  return true;
}