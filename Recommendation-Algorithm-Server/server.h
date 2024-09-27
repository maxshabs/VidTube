#ifndef VIDTUBE_PART4_SERVER_H
#define VIDTUBE_PART4_SERVER_H

#include <vector>
#include <string>
#include <map>

// Function declarations
void handle_client(int client_sock);
std::string extract_user_id(const std::string& message);
std::string extract_video_id(const std::string& message);
std::string join_recommendations(const std::vector<std::string>& recommendations);
std::vector<std::string> get_recommendations(const std::string& user_id, const std::string& video_id);
void parse_video_ids(const std::string& message);

// Global variables
extern std::map<std::string, std::vector<std::string>> userHistory;  // Maps user IDs to video IDs they watched
extern std::vector<std::string> videos;  // List of all video IDs (as strings)

#endif //VIDTUBE_PART4_SERVER_H
