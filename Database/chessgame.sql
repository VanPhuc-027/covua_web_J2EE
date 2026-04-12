-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Apr 12, 2026 at 02:00 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `chessgame`
--

-- --------------------------------------------------------

--
-- Table structure for table `games`
--

CREATE TABLE `games` (
  `id` varchar(255) NOT NULL,
  `black_elo_change` int(11) NOT NULL,
  `black_time_left` int(11) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `current_fen` varchar(255) DEFAULT 'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR',
  `current_turn` enum('BLACK','WHITE') DEFAULT NULL,
  `finished_at` datetime(6) DEFAULT NULL,
  `game_mode` enum('Player_VS_AI','Player_VS_Player') DEFAULT NULL,
  `result` enum('BLACK_WINS','DRAW','WHITE_WINS') DEFAULT NULL,
  `started_at` datetime(6) DEFAULT NULL,
  `status` enum('FINISHED','IN_PROGRESS','WAITING') DEFAULT NULL,
  `termination` enum('AGREEMENT','CHECKMATE','FORFEIT','NORMAL','RESIGNATION','STALEMATE','TIMEOUT') DEFAULT NULL,
  `time_limit_seconds` int(11) NOT NULL,
  `white_elo_change` int(11) NOT NULL,
  `white_time_left` int(11) NOT NULL,
  `black_player_id` bigint(20) DEFAULT NULL,
  `white_player_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `games`
--

INSERT INTO `games` (`id`, `black_elo_change`, `black_time_left`, `created_at`, `current_fen`, `current_turn`, `finished_at`, `game_mode`, `result`, `started_at`, `status`, `termination`, `time_limit_seconds`, `white_elo_change`, `white_time_left`, `black_player_id`, `white_player_id`) VALUES
('080b85d3-3ae3-472d-b3e5-768140872b86', 0, 600, '2026-04-12 08:23:16.000000', 'r1bqkbnr/1p1ppppp/2n5/8/PpPpPP2/8/6PP/RNBQKBNR b KQkq f3 0 1', 'BLACK', '2026-04-12 08:23:43.000000', 'Player_VS_Player', 'BLACK_WINS', '2026-04-12 08:23:16.000000', 'FINISHED', 'RESIGNATION', 600, 0, 600, 4, 2),
('2673a480-ba95-4fd3-a829-265535fd346f', 0, 600, '2026-04-12 07:23:17.000000', 'r3kbnr/pp4pp/2np4/2p1Pb2/2P5/N5qP/PP1PK3/R1BQ1BNR b kq - 0 1', 'BLACK', '2026-04-12 07:24:05.000000', 'Player_VS_Player', 'BLACK_WINS', '2026-04-12 07:23:17.000000', 'FINISHED', 'CHECKMATE', 600, 0, 600, 4, 2),
('9245b7d8-697d-4e37-9c6b-449aa44496e6', 0, 600, '2026-04-12 11:52:52.000000', 'rnbqkbnr/ppp1pppp/8/8/4p1P1/8/PPPP1P1P/RNBQKBNR w KQkq - 0 1', 'WHITE', '2026-04-12 11:53:02.000000', 'Player_VS_Player', 'BLACK_WINS', '2026-04-12 11:52:52.000000', 'FINISHED', 'RESIGNATION', 600, 0, 600, 4, 2),
('94852d36-5c2f-4ca6-b2e6-d70fbe9d5e2a', 0, 600, '2026-04-12 11:53:11.000000', 'rnbqkbnr/p1pppppp/8/8/2p4P/8/PP1PPPP1/RNBQKBNR w KQkq - 0 1', 'WHITE', '2026-04-12 11:53:46.000000', 'Player_VS_Player', 'DRAW', '2026-04-12 11:53:19.000000', 'FINISHED', 'AGREEMENT', 600, 0, 600, 3, 2),
('9ddd14f0-f2f8-49b5-b8cd-dea57c7f2c6b', 0, 600, '2026-04-12 11:47:40.000000', 'r2r2k1/pp3ppp/2n2n2/1B2p1BP/Pb6/1b3N2/3N1PP1/2qQK2R b - - 0 1', 'BLACK', '2026-04-12 11:52:06.000000', 'Player_VS_Player', 'BLACK_WINS', '2026-04-12 11:47:40.000000', 'FINISHED', 'CHECKMATE', 600, 0, 600, 4, 2),
('add5fbc3-353b-418b-a7f0-8e363afc3343', 0, 600, '2026-04-12 11:52:36.000000', 'rnbqkbnr/ppp1pppp/8/3p4/7P/8/PPPPPPP1/RNBQKBNR w KQkq d6 0 1', 'WHITE', '2026-04-12 11:52:41.000000', 'Player_VS_Player', 'BLACK_WINS', '2026-04-12 11:52:36.000000', 'FINISHED', 'RESIGNATION', 600, 0, 600, 4, 2),
('af1790a4-80ee-4c8b-87ff-cc9bd7110c40', 0, 600, '2026-04-12 08:24:45.000000', 'rn1qk2r/pppb1ppp/3bp2n/3p4/3PP2P/N1P2P1N/P2K2B1/3R3R b kq - 0 1', 'BLACK', '2026-04-12 08:26:40.000000', 'Player_VS_Player', 'BLACK_WINS', '2026-04-12 08:24:45.000000', 'FINISHED', 'RESIGNATION', 600, 0, 600, 4, 2),
('d6bea5a7-fe19-46a9-b98a-6f90829a12e9', 0, 600, '2026-04-12 11:25:33.000000', 'rnb1kb1r/ppp1pppp/5n2/8/4P2P/8/PPP2PP1/RNBqKBNR w KQkq - 0 1', 'WHITE', '2026-04-12 11:25:50.000000', 'Player_VS_Player', 'BLACK_WINS', '2026-04-12 11:25:33.000000', 'FINISHED', 'RESIGNATION', 600, 0, 600, 4, 2),
('e323f373-00e9-48e6-87ce-30d5441e223f', 0, 600, '2026-04-12 08:50:38.000000', 'r3kbnr/ppp2ppp/2n1p3/3p4/1PPPP3/8/P2Kq3/RNB5 w kq - 0 1', 'WHITE', '2026-04-12 08:51:30.000000', 'Player_VS_Player', 'BLACK_WINS', '2026-04-12 08:50:38.000000', 'FINISHED', 'RESIGNATION', 600, 0, 600, 4, 2),
('f93a4b4c-3627-446f-95ae-a04d80b74ab5', 0, 600, '2026-04-12 07:37:19.000000', 'rn2kbnr/ppp2ppp/3qp3/3p3b/3P4/4PP2/PPPK3P/RNBQ1B1R w kq - 0 1', 'WHITE', '2026-04-12 07:37:55.000000', 'Player_VS_Player', 'BLACK_WINS', '2026-04-12 07:37:19.000000', 'FINISHED', 'RESIGNATION', 600, 0, 600, 4, 2);

-- --------------------------------------------------------

--
-- Table structure for table `move`
--

CREATE TABLE `move` (
  `id` bigint(20) NOT NULL,
  `move_notation` varchar(255) DEFAULT NULL,
  `move_order` int(11) NOT NULL,
  `timestamp` datetime(6) DEFAULT NULL,
  `game_id` varchar(255) DEFAULT NULL,
  `player_id` bigint(20) DEFAULT NULL,
  `fen_after_move` varchar(255) DEFAULT NULL,
  `uci_move` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `move`
--

INSERT INTO `move` (`id`, `move_notation`, `move_order`, `timestamp`, `game_id`, `player_id`, `fen_after_move`, `uci_move`) VALUES
(101, 'e4', 1, '2026-04-12 07:23:19.000000', '2673a480-ba95-4fd3-a829-265535fd346f', NULL, 'rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1', NULL),
(102, 'c5', 2, '2026-04-12 07:23:21.000000', '2673a480-ba95-4fd3-a829-265535fd346f', NULL, 'rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 1', NULL),
(103, 'c4', 3, '2026-04-12 07:23:23.000000', '2673a480-ba95-4fd3-a829-265535fd346f', NULL, 'rnbqkbnr/pp1ppppp/8/2p5/2P1P3/8/PP1P1PPP/RNBQKBNR b KQkq c3 0 1', NULL),
(104, 'Nc6', 4, '2026-04-12 07:23:24.000000', '2673a480-ba95-4fd3-a829-265535fd346f', NULL, 'r1bqkbnr/pp1ppppp/2n5/2p5/2P1P3/8/PP1P1PPP/RNBQKBNR w KQkq - 0 1', NULL),
(105, 'Na3', 5, '2026-04-12 07:23:30.000000', '2673a480-ba95-4fd3-a829-265535fd346f', NULL, 'r1bqkbnr/pp1ppppp/2n5/2p5/2P1P3/N7/PP1P1PPP/R1BQKBNR b KQkq - 0 1', NULL),
(106, 'd6', 6, '2026-04-12 07:23:32.000000', '2673a480-ba95-4fd3-a829-265535fd346f', NULL, 'r1bqkbnr/pp2pppp/2np4/2p5/2P1P3/N7/PP1P1PPP/R1BQKBNR w KQkq - 0 1', NULL),
(107, 'f4', 7, '2026-04-12 07:23:39.000000', '2673a480-ba95-4fd3-a829-265535fd346f', NULL, 'r1bqkbnr/pp2pppp/2np4/2p5/2P1PP2/N7/PP1P2PP/R1BQKBNR b KQkq f3 0 1', NULL),
(108, 'f5', 8, '2026-04-12 07:23:40.000000', '2673a480-ba95-4fd3-a829-265535fd346f', NULL, 'r1bqkbnr/pp2p1pp/2np4/2p2p2/2P1PP2/N7/PP1P2PP/R1BQKBNR w KQkq f6 0 1', NULL),
(109, 'xf5', 9, '2026-04-12 07:23:42.000000', '2673a480-ba95-4fd3-a829-265535fd346f', NULL, 'r1bqkbnr/pp2p1pp/2np4/2p2P2/2P2P2/N7/PP1P2PP/R1BQKBNR b KQkq - 0 1', NULL),
(110, 'Bxf5', 10, '2026-04-12 07:23:43.000000', '2673a480-ba95-4fd3-a829-265535fd346f', NULL, 'r2qkbnr/pp2p1pp/2np4/2p2b2/2P2P2/N7/PP1P2PP/R1BQKBNR w KQkq - 0 1', NULL),
(111, 'h3', 11, '2026-04-12 07:23:48.000000', '2673a480-ba95-4fd3-a829-265535fd346f', NULL, 'r2qkbnr/pp2p1pp/2np4/2p2b2/2P2P2/N6P/PP1P2P1/R1BQKBNR b KQkq - 0 1', NULL),
(112, 'e5', 12, '2026-04-12 07:23:50.000000', '2673a480-ba95-4fd3-a829-265535fd346f', NULL, 'r2qkbnr/pp4pp/2np4/2p1pb2/2P2P2/N6P/PP1P2P1/R1BQKBNR w KQkq e6 0 1', NULL),
(113, 'xe5', 13, '2026-04-12 07:23:53.000000', '2673a480-ba95-4fd3-a829-265535fd346f', NULL, 'r2qkbnr/pp4pp/2np4/2p1Pb2/2P5/N6P/PP1P2P1/R1BQKBNR b KQkq - 0 1', NULL),
(114, 'Qh4', 14, '2026-04-12 07:23:53.000000', '2673a480-ba95-4fd3-a829-265535fd346f', NULL, 'r3kbnr/pp4pp/2np4/2p1Pb2/2P4q/N6P/PP1P2P1/R1BQKBNR w KQkq - 0 1', NULL),
(115, 'g3', 15, '2026-04-12 07:23:56.000000', '2673a480-ba95-4fd3-a829-265535fd346f', NULL, 'r3kbnr/pp4pp/2np4/2p1Pb2/2P4q/N5PP/PP1P4/R1BQKBNR b KQkq - 0 1', NULL),
(116, 'Qxg3', 16, '2026-04-12 07:23:57.000000', '2673a480-ba95-4fd3-a829-265535fd346f', NULL, 'r3kbnr/pp4pp/2np4/2p1Pb2/2P5/N5qP/PP1P4/R1BQKBNR w KQkq - 0 1', NULL),
(117, 'Ke2', 17, '2026-04-12 07:24:03.000000', '2673a480-ba95-4fd3-a829-265535fd346f', NULL, 'r3kbnr/pp4pp/2np4/2p1Pb2/2P5/N5qP/PP1PK3/R1BQ1BNR b kq - 0 1', NULL),
(118, 'Nd4', 18, '2026-04-12 07:24:05.000000', '2673a480-ba95-4fd3-a829-265535fd346f', NULL, 'r3kbnr/pp4pp/3p4/2p1Pb2/2Pn4/N5qP/PP1PK3/R1BQ1BNR w kq - 0 1', NULL),
(119, 'g4', 1, '2026-04-12 07:37:21.000000', 'f93a4b4c-3627-446f-95ae-a04d80b74ab5', NULL, 'rnbqkbnr/pppppppp/8/8/6P1/8/PPPPPP1P/RNBQKBNR b KQkq g3 0 1', 'g2g4'),
(120, 'd5', 2, '2026-04-12 07:37:22.000000', 'f93a4b4c-3627-446f-95ae-a04d80b74ab5', NULL, 'rnbqkbnr/ppp1pppp/8/3p4/6P1/8/PPPPPP1P/RNBQKBNR w KQkq d6 0 1', 'd7d5'),
(121, 'd4', 3, '2026-04-12 07:37:24.000000', 'f93a4b4c-3627-446f-95ae-a04d80b74ab5', NULL, 'rnbqkbnr/ppp1pppp/8/3p4/3P2P1/8/PPP1PP1P/RNBQKBNR b KQkq d3 0 1', 'd2d4'),
(122, 'Bxg4', 4, '2026-04-12 07:37:24.000000', 'f93a4b4c-3627-446f-95ae-a04d80b74ab5', NULL, 'rn1qkbnr/ppp1pppp/8/3p4/3P2b1/8/PPP1PP1P/RNBQKBNR w KQkq - 0 1', 'c8g4'),
(123, 'f3', 5, '2026-04-12 07:37:26.000000', 'f93a4b4c-3627-446f-95ae-a04d80b74ab5', NULL, 'rn1qkbnr/ppp1pppp/8/3p4/3P2b1/5P2/PPP1P2P/RNBQKBNR b KQkq - 0 1', 'f2f3'),
(124, 'Bh5', 6, '2026-04-12 07:37:27.000000', 'f93a4b4c-3627-446f-95ae-a04d80b74ab5', NULL, 'rn1qkbnr/ppp1pppp/8/3p3b/3P4/5P2/PPP1P2P/RNBQKBNR w KQkq - 0 1', 'g4h5'),
(125, 'Nh3', 7, '2026-04-12 07:37:31.000000', 'f93a4b4c-3627-446f-95ae-a04d80b74ab5', NULL, 'rn1qkbnr/ppp1pppp/8/3p3b/3P4/5P1N/PPP1P2P/RNBQKB1R b KQkq - 0 1', 'g1h3'),
(126, 'e6', 8, '2026-04-12 07:37:31.000000', 'f93a4b4c-3627-446f-95ae-a04d80b74ab5', NULL, 'rn1qkbnr/ppp2ppp/4p3/3p3b/3P4/5P1N/PPP1P2P/RNBQKB1R w KQkq - 0 1', 'e7e6'),
(127, 'Nf4', 9, '2026-04-12 07:37:35.000000', 'f93a4b4c-3627-446f-95ae-a04d80b74ab5', NULL, 'rn1qkbnr/ppp2ppp/4p3/3p3b/3P1N2/5P2/PPP1P2P/RNBQKB1R b KQkq - 0 1', 'h3f4'),
(128, 'Qh4', 10, '2026-04-12 07:37:36.000000', 'f93a4b4c-3627-446f-95ae-a04d80b74ab5', NULL, 'rn2kbnr/ppp2ppp/4p3/3p3b/3P1N1q/5P2/PPP1P2P/RNBQKB1R w KQkq - 0 1', 'd8h4'),
(129, 'Kd2', 11, '2026-04-12 07:37:45.000000', 'f93a4b4c-3627-446f-95ae-a04d80b74ab5', NULL, 'rn2kbnr/ppp2ppp/4p3/3p3b/3P1N1q/5P2/PPPKP2P/RNBQ1B1R b kq - 0 1', 'e1d2'),
(130, 'Qxf4', 12, '2026-04-12 07:37:46.000000', 'f93a4b4c-3627-446f-95ae-a04d80b74ab5', NULL, 'rn2kbnr/ppp2ppp/4p3/3p3b/3P1q2/5P2/PPPKP2P/RNBQ1B1R w kq - 0 1', 'h4f4'),
(131, 'e3', 13, '2026-04-12 07:37:51.000000', 'f93a4b4c-3627-446f-95ae-a04d80b74ab5', NULL, 'rn2kbnr/ppp2ppp/4p3/3p3b/3P1q2/4PP2/PPPK3P/RNBQ1B1R b kq - 0 1', 'e2e3'),
(132, 'Qd6', 14, '2026-04-12 07:37:52.000000', 'f93a4b4c-3627-446f-95ae-a04d80b74ab5', NULL, 'rn2kbnr/ppp2ppp/3qp3/3p3b/3P4/4PP2/PPPK3P/RNBQ1B1R w kq - 0 1', 'f4d6'),
(133, 'e4', 1, '2026-04-12 08:23:18.000000', '080b85d3-3ae3-472d-b3e5-768140872b86', NULL, 'rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1', 'e2e4'),
(134, 'c5', 2, '2026-04-12 08:23:20.000000', '080b85d3-3ae3-472d-b3e5-768140872b86', NULL, 'rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 1', 'c7c5'),
(135, 'c4', 3, '2026-04-12 08:23:22.000000', '080b85d3-3ae3-472d-b3e5-768140872b86', NULL, 'rnbqkbnr/pp1ppppp/8/2p5/2P1P3/8/PP1P1PPP/RNBQKBNR b KQkq c3 0 1', 'c2c4'),
(136, 'Nc6', 4, '2026-04-12 08:23:23.000000', '080b85d3-3ae3-472d-b3e5-768140872b86', NULL, 'r1bqkbnr/pp1ppppp/2n5/2p5/2P1P3/8/PP1P1PPP/RNBQKBNR w KQkq - 0 1', 'b8c6'),
(137, 'd4', 5, '2026-04-12 08:23:23.000000', '080b85d3-3ae3-472d-b3e5-768140872b86', NULL, 'r1bqkbnr/pp1ppppp/2n5/2p5/2PPP3/8/PP3PPP/RNBQKBNR b KQkq d3 0 1', 'd2d4'),
(138, 'xd4', 6, '2026-04-12 08:23:24.000000', '080b85d3-3ae3-472d-b3e5-768140872b86', NULL, 'r1bqkbnr/pp1ppppp/2n5/8/2PpP3/8/PP3PPP/RNBQKBNR w KQkq - 0 1', 'c5d4'),
(139, 'b4', 7, '2026-04-12 08:23:24.000000', '080b85d3-3ae3-472d-b3e5-768140872b86', NULL, 'r1bqkbnr/pp1ppppp/2n5/8/1PPpP3/8/P4PPP/RNBQKBNR b KQkq b3 0 1', 'b2b4'),
(140, 'a5', 8, '2026-04-12 08:23:25.000000', '080b85d3-3ae3-472d-b3e5-768140872b86', NULL, 'r1bqkbnr/1p1ppppp/2n5/p7/1PPpP3/8/P4PPP/RNBQKBNR w KQkq a6 0 1', 'a7a5'),
(141, 'a4', 9, '2026-04-12 08:23:25.000000', '080b85d3-3ae3-472d-b3e5-768140872b86', NULL, 'r1bqkbnr/1p1ppppp/2n5/p7/PPPpP3/8/5PPP/RNBQKBNR b KQkq a3 0 1', 'a2a4'),
(142, 'xb4', 10, '2026-04-12 08:23:26.000000', '080b85d3-3ae3-472d-b3e5-768140872b86', NULL, 'r1bqkbnr/1p1ppppp/2n5/8/PpPpP3/8/5PPP/RNBQKBNR w KQkq - 0 1', 'a5b4'),
(143, 'f4', 11, '2026-04-12 08:23:28.000000', '080b85d3-3ae3-472d-b3e5-768140872b86', NULL, 'r1bqkbnr/1p1ppppp/2n5/8/PpPpPP2/8/6PP/RNBQKBNR b KQkq f3 0 1', 'f2f4'),
(144, 'g4', 1, '2026-04-12 08:25:00.000000', 'af1790a4-80ee-4c8b-87ff-cc9bd7110c40', NULL, 'rnbqkbnr/pppppppp/8/8/6P1/8/PPPPPP1P/RNBQKBNR b KQkq g3 0 1', 'g2g4'),
(145, 'd5', 2, '2026-04-12 08:25:02.000000', 'af1790a4-80ee-4c8b-87ff-cc9bd7110c40', NULL, 'rnbqkbnr/ppp1pppp/8/3p4/6P1/8/PPPPPP1P/RNBQKBNR w KQkq d6 0 1', 'd7d5'),
(146, 'd4', 3, '2026-04-12 08:25:02.000000', 'af1790a4-80ee-4c8b-87ff-cc9bd7110c40', NULL, 'rnbqkbnr/ppp1pppp/8/3p4/3P2P1/8/PPP1PP1P/RNBQKBNR b KQkq d3 0 1', 'd2d4'),
(147, 'Bxg4', 4, '2026-04-12 08:25:03.000000', 'af1790a4-80ee-4c8b-87ff-cc9bd7110c40', NULL, 'rn1qkbnr/ppp1pppp/8/3p4/3P2b1/8/PPP1PP1P/RNBQKBNR w KQkq - 0 1', 'c8g4'),
(148, 'b4', 5, '2026-04-12 08:25:04.000000', 'af1790a4-80ee-4c8b-87ff-cc9bd7110c40', NULL, 'rn1qkbnr/ppp1pppp/8/3p4/1P1P2b1/8/P1P1PP1P/RNBQKBNR b KQkq b3 0 1', 'b2b4'),
(149, 'e6', 6, '2026-04-12 08:25:05.000000', 'af1790a4-80ee-4c8b-87ff-cc9bd7110c40', NULL, 'rn1qkbnr/ppp2ppp/4p3/3p4/1P1P2b1/8/P1P1PP1P/RNBQKBNR w KQkq - 0 1', 'e7e6'),
(150, 'e4', 7, '2026-04-12 08:25:06.000000', 'af1790a4-80ee-4c8b-87ff-cc9bd7110c40', NULL, 'rn1qkbnr/ppp2ppp/4p3/3p4/1P1PP1b1/8/P1P2P1P/RNBQKBNR b KQkq e3 0 1', 'e2e4'),
(151, 'Bxd1', 8, '2026-04-12 08:25:07.000000', 'af1790a4-80ee-4c8b-87ff-cc9bd7110c40', NULL, 'rn1qkbnr/ppp2ppp/4p3/3p4/1P1PP3/8/P1P2P1P/RNBbKBNR w KQkq - 0 1', 'g4d1'),
(152, 'h4', 9, '2026-04-12 08:25:08.000000', 'af1790a4-80ee-4c8b-87ff-cc9bd7110c40', NULL, 'rn1qkbnr/ppp2ppp/4p3/3p4/1P1PP2P/8/P1P2P2/RNBbKBNR b KQkq h3 0 1', 'h2h4'),
(153, 'Bxb4', 10, '2026-04-12 08:25:09.000000', 'af1790a4-80ee-4c8b-87ff-cc9bd7110c40', NULL, 'rn1qk1nr/ppp2ppp/4p3/3p4/1b1PP2P/8/P1P2P2/RNBbKBNR w KQkq - 0 1', 'f8b4'),
(154, 'c3', 11, '2026-04-12 08:25:11.000000', 'af1790a4-80ee-4c8b-87ff-cc9bd7110c40', NULL, 'rn1qk1nr/ppp2ppp/4p3/3p4/1b1PP2P/2P5/P4P2/RNBbKBNR b KQkq - 0 1', 'c2c3'),
(155, 'Ba4', 12, '2026-04-12 08:25:12.000000', 'af1790a4-80ee-4c8b-87ff-cc9bd7110c40', NULL, 'rn1qk1nr/ppp2ppp/4p3/3p4/bb1PP2P/2P5/P4P2/RNB1KBNR w KQkq - 0 1', 'd1a4'),
(156, 'Bf4', 13, '2026-04-12 08:25:12.000000', 'af1790a4-80ee-4c8b-87ff-cc9bd7110c40', NULL, 'rn1qk1nr/ppp2ppp/4p3/3p4/bb1PPB1P/2P5/P4P2/RN2KBNR b KQkq - 0 1', 'c1f4'),
(157, 'Bd6', 14, '2026-04-12 08:25:13.000000', 'af1790a4-80ee-4c8b-87ff-cc9bd7110c40', NULL, 'rn1qk1nr/ppp2ppp/3bp3/3p4/b2PPB1P/2P5/P4P2/RN2KBNR w KQkq - 0 1', 'b4d6'),
(158, 'f3', 15, '2026-04-12 08:25:14.000000', 'af1790a4-80ee-4c8b-87ff-cc9bd7110c40', NULL, 'rn1qk1nr/ppp2ppp/3bp3/3p4/b2PPB1P/2P2P2/P7/RN2KBNR b KQkq - 0 1', 'f2f3'),
(159, 'Bxf4', 16, '2026-04-12 08:25:15.000000', 'af1790a4-80ee-4c8b-87ff-cc9bd7110c40', NULL, 'rn1qk1nr/ppp2ppp/4p3/3p4/b2PPb1P/2P2P2/P7/RN2KBNR w KQkq - 0 1', 'd6f4'),
(160, 'Nh3', 17, '2026-04-12 08:25:16.000000', 'af1790a4-80ee-4c8b-87ff-cc9bd7110c40', NULL, 'rn1qk1nr/ppp2ppp/4p3/3p4/b2PPb1P/2P2P1N/P7/RN2KB1R b KQkq - 0 1', 'g1h3'),
(161, 'Bd6', 18, '2026-04-12 08:25:17.000000', 'af1790a4-80ee-4c8b-87ff-cc9bd7110c40', NULL, 'rn1qk1nr/ppp2ppp/3bp3/3p4/b2PP2P/2P2P1N/P7/RN2KB1R w KQkq - 0 1', 'f4d6'),
(162, 'Bg2', 19, '2026-04-12 08:25:18.000000', 'af1790a4-80ee-4c8b-87ff-cc9bd7110c40', NULL, 'rn1qk1nr/ppp2ppp/3bp3/3p4/b2PP2P/2P2P1N/P5B1/RN2K2R b KQkq - 0 1', 'f1g2'),
(163, 'Nh6', 20, '2026-04-12 08:25:19.000000', 'af1790a4-80ee-4c8b-87ff-cc9bd7110c40', NULL, 'rn1qk2r/ppp2ppp/3bp2n/3p4/b2PP2P/2P2P1N/P5B1/RN2K2R w KQkq - 0 1', 'g8h6'),
(164, 'Na3', 21, '2026-04-12 08:25:20.000000', 'af1790a4-80ee-4c8b-87ff-cc9bd7110c40', NULL, 'rn1qk2r/ppp2ppp/3bp2n/3p4/b2PP2P/N1P2P1N/P5B1/R3K2R b KQkq - 0 1', 'b1a3'),
(165, 'Bc6', 22, '2026-04-12 08:25:21.000000', 'af1790a4-80ee-4c8b-87ff-cc9bd7110c40', NULL, 'rn1qk2r/ppp2ppp/2bbp2n/3p4/3PP2P/N1P2P1N/P5B1/R3K2R w KQkq - 0 1', 'a4c6'),
(166, 'Rd1', 23, '2026-04-12 08:25:22.000000', 'af1790a4-80ee-4c8b-87ff-cc9bd7110c40', NULL, 'rn1qk2r/ppp2ppp/2bbp2n/3p4/3PP2P/N1P2P1N/P5B1/3RK2R b Kkq - 0 1', 'a1d1'),
(167, 'Bd7', 24, '2026-04-12 08:25:23.000000', 'af1790a4-80ee-4c8b-87ff-cc9bd7110c40', NULL, 'rn1qk2r/pppb1ppp/3bp2n/3p4/3PP2P/N1P2P1N/P5B1/3RK2R w Kkq - 0 1', 'c6d7'),
(168, 'Kd2', 25, '2026-04-12 08:25:23.000000', 'af1790a4-80ee-4c8b-87ff-cc9bd7110c40', NULL, 'rn1qk2r/pppb1ppp/3bp2n/3p4/3PP2P/N1P2P1N/P2K2B1/3R3R b kq - 0 1', 'e1d2'),
(169, 'f4', 1, '2026-04-12 08:50:40.000000', 'e323f373-00e9-48e6-87ce-30d5441e223f', NULL, 'rnbqkbnr/pppppppp/8/8/5P2/8/PPPPP1PP/RNBQKBNR b KQkq f3 0 1', 'f2f4'),
(170, 'd5', 2, '2026-04-12 08:50:42.000000', 'e323f373-00e9-48e6-87ce-30d5441e223f', NULL, 'rnbqkbnr/ppp1pppp/8/3p4/5P2/8/PPPPP1PP/RNBQKBNR w KQkq d6 0 1', 'd7d5'),
(171, 'g4', 3, '2026-04-12 08:50:43.000000', 'e323f373-00e9-48e6-87ce-30d5441e223f', NULL, 'rnbqkbnr/ppp1pppp/8/3p4/5PP1/8/PPPPP2P/RNBQKBNR b KQkq g3 0 1', 'g2g4'),
(172, 'Bxg4', 4, '2026-04-12 08:50:44.000000', 'e323f373-00e9-48e6-87ce-30d5441e223f', NULL, 'rn1qkbnr/ppp1pppp/8/3p4/5Pb1/8/PPPPP2P/RNBQKBNR w KQkq - 0 1', 'c8g4'),
(173, 'd4', 5, '2026-04-12 08:50:45.000000', 'e323f373-00e9-48e6-87ce-30d5441e223f', NULL, 'rn1qkbnr/ppp1pppp/8/3p4/3P1Pb1/8/PPP1P2P/RNBQKBNR b KQkq d3 0 1', 'd2d4'),
(174, 'e6', 6, '2026-04-12 08:50:46.000000', 'e323f373-00e9-48e6-87ce-30d5441e223f', NULL, 'rn1qkbnr/ppp2ppp/4p3/3p4/3P1Pb1/8/PPP1P2P/RNBQKBNR w KQkq - 0 1', 'e7e6'),
(175, 'e4', 7, '2026-04-12 08:50:47.000000', 'e323f373-00e9-48e6-87ce-30d5441e223f', NULL, 'rn1qkbnr/ppp2ppp/4p3/3p4/3PPPb1/8/PPP4P/RNBQKBNR b KQkq e3 0 1', 'e2e4'),
(176, 'Qh4', 8, '2026-04-12 08:50:48.000000', 'e323f373-00e9-48e6-87ce-30d5441e223f', NULL, 'rn2kbnr/ppp2ppp/4p3/3p4/3PPPbq/8/PPP4P/RNBQKBNR w KQkq - 0 1', 'd8h4'),
(177, 'Kd2', 9, '2026-04-12 08:50:50.000000', 'e323f373-00e9-48e6-87ce-30d5441e223f', NULL, 'rn2kbnr/ppp2ppp/4p3/3p4/3PPPbq/8/PPPK3P/RNBQ1BNR b kq - 0 1', 'e1d2'),
(178, 'Bxd1', 10, '2026-04-12 08:50:51.000000', 'e323f373-00e9-48e6-87ce-30d5441e223f', NULL, 'rn2kbnr/ppp2ppp/4p3/3p4/3PPP1q/8/PPPK3P/RNBb1BNR w kq - 0 1', 'g4d1'),
(179, 'b4', 11, '2026-04-12 08:50:51.000000', 'e323f373-00e9-48e6-87ce-30d5441e223f', NULL, 'rn2kbnr/ppp2ppp/4p3/3p4/1P1PPP1q/8/P1PK3P/RNBb1BNR b kq b3 0 1', 'b2b4'),
(180, 'Qxf4', 12, '2026-04-12 08:50:52.000000', 'e323f373-00e9-48e6-87ce-30d5441e223f', NULL, 'rn2kbnr/ppp2ppp/4p3/3p4/1P1PPq2/8/P1PK3P/RNBb1BNR w kq - 0 1', 'h4f4'),
(181, 'Kxd1', 13, '2026-04-12 08:50:58.000000', 'e323f373-00e9-48e6-87ce-30d5441e223f', NULL, 'rn2kbnr/ppp2ppp/4p3/3p4/1P1PPq2/8/P1P4P/RNBK1BNR b kq - 0 1', 'd2d1'),
(182, 'Qxf1', 14, '2026-04-12 08:50:59.000000', 'e323f373-00e9-48e6-87ce-30d5441e223f', NULL, 'rn2kbnr/ppp2ppp/4p3/3p4/1P1PP3/8/P1P4P/RNBK1qNR w kq - 0 1', 'f4f1'),
(183, 'Kd2', 15, '2026-04-12 08:51:02.000000', 'e323f373-00e9-48e6-87ce-30d5441e223f', NULL, 'rn2kbnr/ppp2ppp/4p3/3p4/1P1PP3/8/P1PK3P/RNB2qNR b kq - 0 1', 'd1d2'),
(184, 'Qg2', 16, '2026-04-12 08:51:03.000000', 'e323f373-00e9-48e6-87ce-30d5441e223f', NULL, 'rn2kbnr/ppp2ppp/4p3/3p4/1P1PP3/8/P1PK2qP/RNB3NR w kq - 0 1', 'f1g2'),
(185, 'Kd1', 17, '2026-04-12 08:51:07.000000', 'e323f373-00e9-48e6-87ce-30d5441e223f', NULL, 'rn2kbnr/ppp2ppp/4p3/3p4/1P1PP3/8/P1P3qP/RNBK2NR b kq - 0 1', 'd2d1'),
(186, 'Nc6', 18, '2026-04-12 08:51:08.000000', 'e323f373-00e9-48e6-87ce-30d5441e223f', NULL, 'r3kbnr/ppp2ppp/2n1p3/3p4/1P1PP3/8/P1P3qP/RNBK2NR w kq - 0 1', 'b8c6'),
(187, 'Ne2', 19, '2026-04-12 08:51:14.000000', 'e323f373-00e9-48e6-87ce-30d5441e223f', NULL, 'r3kbnr/ppp2ppp/2n1p3/3p4/1P1PP3/8/P1P1N1qP/RNBK3R b kq - 0 1', 'g1e2'),
(188, 'Qxh1', 20, '2026-04-12 08:51:16.000000', 'e323f373-00e9-48e6-87ce-30d5441e223f', NULL, 'r3kbnr/ppp2ppp/2n1p3/3p4/1P1PP3/8/P1P1N2P/RNBK3q w kq - 0 1', 'g2h1'),
(189, 'Kd2', 21, '2026-04-12 08:51:18.000000', 'e323f373-00e9-48e6-87ce-30d5441e223f', NULL, 'r3kbnr/ppp2ppp/2n1p3/3p4/1P1PP3/8/P1PKN2P/RNB4q b kq - 0 1', 'd1d2'),
(190, 'Qxh2', 22, '2026-04-12 08:51:19.000000', 'e323f373-00e9-48e6-87ce-30d5441e223f', NULL, 'r3kbnr/ppp2ppp/2n1p3/3p4/1P1PP3/8/P1PKN2q/RNB5 w kq - 0 1', 'h1h2'),
(191, 'c4', 23, '2026-04-12 08:51:21.000000', 'e323f373-00e9-48e6-87ce-30d5441e223f', NULL, 'r3kbnr/ppp2ppp/2n1p3/3p4/1PPPP3/8/P2KN2q/RNB5 b kq c3 0 1', 'c2c4'),
(192, 'Qxe2', 24, '2026-04-12 08:51:22.000000', 'e323f373-00e9-48e6-87ce-30d5441e223f', NULL, 'r3kbnr/ppp2ppp/2n1p3/3p4/1PPPP3/8/P2Kq3/RNB5 w kq - 0 1', 'h2e2'),
(193, 'h4', 1, '2026-04-12 11:25:36.000000', 'd6bea5a7-fe19-46a9-b98a-6f90829a12e9', NULL, 'rnbqkbnr/pppppppp/8/8/7P/8/PPPPPPP1/RNBQKBNR b KQkq h3 0 1', 'h2h4'),
(194, 'd5', 2, '2026-04-12 11:25:39.000000', 'd6bea5a7-fe19-46a9-b98a-6f90829a12e9', NULL, 'rnbqkbnr/ppp1pppp/8/3p4/7P/8/PPPPPPP1/RNBQKBNR w KQkq d6 0 1', 'd7d5'),
(195, 'e4', 3, '2026-04-12 11:25:40.000000', 'd6bea5a7-fe19-46a9-b98a-6f90829a12e9', NULL, 'rnbqkbnr/ppp1pppp/8/3p4/4P2P/8/PPPP1PP1/RNBQKBNR b KQkq e3 0 1', 'e2e4'),
(196, 'xe4', 4, '2026-04-12 11:25:41.000000', 'd6bea5a7-fe19-46a9-b98a-6f90829a12e9', NULL, 'rnbqkbnr/ppp1pppp/8/8/4p2P/8/PPPP1PP1/RNBQKBNR w KQkq - 0 1', 'd5e4'),
(197, 'd3', 5, '2026-04-12 11:25:44.000000', 'd6bea5a7-fe19-46a9-b98a-6f90829a12e9', NULL, 'rnbqkbnr/ppp1pppp/8/8/4p2P/3P4/PPP2PP1/RNBQKBNR b KQkq - 0 1', 'd2d3'),
(198, 'Nf6', 6, '2026-04-12 11:25:44.000000', 'd6bea5a7-fe19-46a9-b98a-6f90829a12e9', NULL, 'rnbqkb1r/ppp1pppp/5n2/8/4p2P/3P4/PPP2PP1/RNBQKBNR w KQkq - 0 1', 'g8f6'),
(199, 'xe4', 7, '2026-04-12 11:25:46.000000', 'd6bea5a7-fe19-46a9-b98a-6f90829a12e9', NULL, 'rnbqkb1r/ppp1pppp/5n2/8/4P2P/8/PPP2PP1/RNBQKBNR b KQkq - 0 1', 'd3e4'),
(200, 'Qxd1', 8, '2026-04-12 11:25:48.000000', 'd6bea5a7-fe19-46a9-b98a-6f90829a12e9', NULL, 'rnb1kb1r/ppp1pppp/5n2/8/4P2P/8/PPP2PP1/RNBqKBNR w KQkq - 0 1', 'd8d1'),
(201, 'd4', 1, '2026-04-12 11:47:43.000000', '9ddd14f0-f2f8-49b5-b8cd-dea57c7f2c6b', NULL, 'rnbqkbnr/pppppppp/8/8/3P4/8/PPP1PPPP/RNBQKBNR b KQkq d3 0 1', 'd2d4'),
(202, 'd5', 2, '2026-04-12 11:47:45.000000', '9ddd14f0-f2f8-49b5-b8cd-dea57c7f2c6b', NULL, 'rnbqkbnr/ppp1pppp/8/3p4/3P4/8/PPP1PPPP/RNBQKBNR w KQkq d6 0 1', 'd7d5'),
(203, 'e3', 3, '2026-04-12 11:47:48.000000', '9ddd14f0-f2f8-49b5-b8cd-dea57c7f2c6b', NULL, 'rnbqkbnr/ppp1pppp/8/3p4/3P4/4P3/PPP2PPP/RNBQKBNR b KQkq - 0 1', 'e2e3'),
(204, 'Nf6', 4, '2026-04-12 11:47:49.000000', '9ddd14f0-f2f8-49b5-b8cd-dea57c7f2c6b', NULL, 'rnbqkb1r/ppp1pppp/5n2/3p4/3P4/4P3/PPP2PPP/RNBQKBNR w KQkq - 0 1', 'g8f6'),
(205, 'c4', 5, '2026-04-12 11:47:52.000000', '9ddd14f0-f2f8-49b5-b8cd-dea57c7f2c6b', NULL, 'rnbqkb1r/ppp1pppp/5n2/3p4/2PP4/4P3/PP3PPP/RNBQKBNR b KQkq c3 0 1', 'c2c4'),
(206, 'e6', 6, '2026-04-12 11:47:53.000000', '9ddd14f0-f2f8-49b5-b8cd-dea57c7f2c6b', NULL, 'rnbqkb1r/ppp2ppp/4pn2/3p4/2PP4/4P3/PP3PPP/RNBQKBNR w KQkq - 0 1', 'e7e6'),
(207, 'a4', 7, '2026-04-12 11:47:53.000000', '9ddd14f0-f2f8-49b5-b8cd-dea57c7f2c6b', NULL, 'rnbqkb1r/ppp2ppp/4pn2/3p4/P1PP4/4P3/1P3PPP/RNBQKBNR b KQkq a3 0 1', 'a2a4'),
(208, 'c5', 8, '2026-04-12 11:47:54.000000', '9ddd14f0-f2f8-49b5-b8cd-dea57c7f2c6b', NULL, 'rnbqkb1r/pp3ppp/4pn2/2pp4/P1PP4/4P3/1P3PPP/RNBQKBNR w KQkq c6 0 1', 'c7c5'),
(209, 'xd5', 9, '2026-04-12 11:48:03.000000', '9ddd14f0-f2f8-49b5-b8cd-dea57c7f2c6b', NULL, 'rnbqkb1r/pp3ppp/4pn2/2pP4/P2P4/4P3/1P3PPP/RNBQKBNR b KQkq - 0 1', 'c4d5'),
(210, 'Nxd5', 10, '2026-04-12 11:48:05.000000', '9ddd14f0-f2f8-49b5-b8cd-dea57c7f2c6b', NULL, 'rnbqkb1r/pp3ppp/4p3/2pn4/P2P4/4P3/1P3PPP/RNBQKBNR w KQkq - 0 1', 'f6d5'),
(211, 'Qh5', 11, '2026-04-12 11:48:19.000000', '9ddd14f0-f2f8-49b5-b8cd-dea57c7f2c6b', NULL, 'rnbqkb1r/pp3ppp/4p3/2pn3Q/P2P4/4P3/1P3PPP/RNB1KBNR b KQkq - 0 1', 'd1h5'),
(212, 'xd4', 12, '2026-04-12 11:48:21.000000', '9ddd14f0-f2f8-49b5-b8cd-dea57c7f2c6b', NULL, 'rnbqkb1r/pp3ppp/4p3/3n3Q/P2p4/4P3/1P3PPP/RNB1KBNR w KQkq - 0 1', 'c5d4'),
(213, 'Bb5', 13, '2026-04-12 11:48:40.000000', '9ddd14f0-f2f8-49b5-b8cd-dea57c7f2c6b', NULL, 'rnbqkb1r/pp3ppp/4p3/1B1n3Q/P2p4/4P3/1P3PPP/RNB1K1NR b KQkq - 0 1', 'f1b5'),
(214, 'Nc6', 14, '2026-04-12 11:48:41.000000', '9ddd14f0-f2f8-49b5-b8cd-dea57c7f2c6b', NULL, 'r1bqkb1r/pp3ppp/2n1p3/1B1n3Q/P2p4/4P3/1P3PPP/RNB1K1NR w KQkq - 0 1', 'b8c6'),
(215, 'xd4', 15, '2026-04-12 11:48:48.000000', '9ddd14f0-f2f8-49b5-b8cd-dea57c7f2c6b', NULL, 'r1bqkb1r/pp3ppp/2n1p3/1B1n3Q/P2P4/8/1P3PPP/RNB1K1NR b KQkq - 0 1', 'e3d4'),
(216, 'Bb4', 16, '2026-04-12 11:48:50.000000', '9ddd14f0-f2f8-49b5-b8cd-dea57c7f2c6b', NULL, 'r1bqk2r/pp3ppp/2n1p3/1B1n3Q/Pb1P4/8/1P3PPP/RNB1K1NR w KQkq - 0 1', 'f8b4'),
(217, 'Kd1', 17, '2026-04-12 11:48:55.000000', '9ddd14f0-f2f8-49b5-b8cd-dea57c7f2c6b', NULL, 'r1bqk2r/pp3ppp/2n1p3/1B1n3Q/Pb1P4/8/1P3PPP/RNBK2NR b kq - 0 1', 'e1d1'),
(218, 'Nf6', 18, '2026-04-12 11:48:56.000000', '9ddd14f0-f2f8-49b5-b8cd-dea57c7f2c6b', NULL, 'r1bqk2r/pp3ppp/2n1pn2/1B5Q/Pb1P4/8/1P3PPP/RNBK2NR w kq - 0 1', 'd5f6'),
(219, 'Qe2', 19, '2026-04-12 11:49:23.000000', '9ddd14f0-f2f8-49b5-b8cd-dea57c7f2c6b', NULL, 'r1bqk2r/pp3ppp/2n1pn2/1B6/Pb1P4/8/1P2QPPP/RNBK2NR b kq - 0 1', 'h5e2'),
(220, 'O-O', 20, '2026-04-12 11:49:25.000000', '9ddd14f0-f2f8-49b5-b8cd-dea57c7f2c6b', NULL, 'r1bq1rk1/pp3ppp/2n1pn2/1B6/Pb1P4/8/1P2QPPP/RNBK2NR w - - 0 1', 'e8g8'),
(221, 'Bg5', 21, '2026-04-12 11:49:43.000000', '9ddd14f0-f2f8-49b5-b8cd-dea57c7f2c6b', NULL, 'r1bq1rk1/pp3ppp/2n1pn2/1B4B1/Pb1P4/8/1P2QPPP/RN1K2NR b - - 0 1', 'c1g5'),
(222, 'Qxd4', 22, '2026-04-12 11:49:44.000000', '9ddd14f0-f2f8-49b5-b8cd-dea57c7f2c6b', NULL, 'r1b2rk1/pp3ppp/2n1pn2/1B4B1/Pb1q4/8/1P2QPPP/RN1K2NR w - - 0 1', 'd8d4'),
(223, 'Nd2', 23, '2026-04-12 11:49:57.000000', '9ddd14f0-f2f8-49b5-b8cd-dea57c7f2c6b', NULL, 'r1b2rk1/pp3ppp/2n1pn2/1B4B1/Pb1q4/8/1P1NQPPP/R2K2NR b - - 0 1', 'b1d2'),
(224, 'Rd8', 24, '2026-04-12 11:49:58.000000', '9ddd14f0-f2f8-49b5-b8cd-dea57c7f2c6b', NULL, 'r1br2k1/pp3ppp/2n1pn2/1B4B1/Pb1q4/8/1P1NQPPP/R2K2NR w - - 0 1', 'f8d8'),
(225, 'Nf3', 25, '2026-04-12 11:50:28.000000', '9ddd14f0-f2f8-49b5-b8cd-dea57c7f2c6b', NULL, 'r1br2k1/pp3ppp/2n1pn2/1B4B1/Pb1q4/5N2/1P1NQPPP/R2K3R b - - 0 1', 'g1f3'),
(226, 'Qxb2', 26, '2026-04-12 11:50:29.000000', '9ddd14f0-f2f8-49b5-b8cd-dea57c7f2c6b', NULL, 'r1br2k1/pp3ppp/2n1pn2/1B4B1/Pb6/5N2/1q1NQPPP/R2K3R w - - 0 1', 'd4b2'),
(227, 'Rc1', 27, '2026-04-12 11:51:08.000000', '9ddd14f0-f2f8-49b5-b8cd-dea57c7f2c6b', NULL, 'r1br2k1/pp3ppp/2n1pn2/1B4B1/Pb6/5N2/1q1NQPPP/2RK3R b - - 0 1', 'a1c1'),
(228, 'e5', 28, '2026-04-12 11:51:09.000000', '9ddd14f0-f2f8-49b5-b8cd-dea57c7f2c6b', NULL, 'r1br2k1/pp3ppp/2n2n2/1B2p1B1/Pb6/5N2/1q1NQPPP/2RK3R w - - 0 1', 'e6e5'),
(229, 'h4', 29, '2026-04-12 11:51:36.000000', '9ddd14f0-f2f8-49b5-b8cd-dea57c7f2c6b', NULL, 'r1br2k1/pp3ppp/2n2n2/1B2p1B1/Pb5P/5N2/1q1NQPP1/2RK3R b - h3 0 1', 'h2h4'),
(230, 'Be6', 30, '2026-04-12 11:51:37.000000', '9ddd14f0-f2f8-49b5-b8cd-dea57c7f2c6b', NULL, 'r2r2k1/pp3ppp/2n1bn2/1B2p1B1/Pb5P/5N2/1q1NQPP1/2RK3R w - - 0 1', 'c8e6'),
(231, 'h5', 31, '2026-04-12 11:51:48.000000', '9ddd14f0-f2f8-49b5-b8cd-dea57c7f2c6b', NULL, 'r2r2k1/pp3ppp/2n1bn2/1B2p1BP/Pb6/5N2/1q1NQPP1/2RK3R b - - 0 1', 'h4h5'),
(232, 'Bb3', 32, '2026-04-12 11:51:49.000000', '9ddd14f0-f2f8-49b5-b8cd-dea57c7f2c6b', NULL, 'r2r2k1/pp3ppp/2n2n2/1B2p1BP/Pb6/1b3N2/1q1NQPP1/2RK3R w - - 0 1', 'e6b3'),
(233, 'Ke1', 33, '2026-04-12 11:51:57.000000', '9ddd14f0-f2f8-49b5-b8cd-dea57c7f2c6b', NULL, 'r2r2k1/pp3ppp/2n2n2/1B2p1BP/Pb6/1b3N2/1q1NQPP1/2R1K2R b - - 0 1', 'd1e1'),
(234, 'Qxc1', 34, '2026-04-12 11:51:59.000000', '9ddd14f0-f2f8-49b5-b8cd-dea57c7f2c6b', NULL, 'r2r2k1/pp3ppp/2n2n2/1B2p1BP/Pb6/1b3N2/3NQPP1/2q1K2R w - - 0 1', 'b2c1'),
(235, 'Qd1', 35, '2026-04-12 11:52:04.000000', '9ddd14f0-f2f8-49b5-b8cd-dea57c7f2c6b', NULL, 'r2r2k1/pp3ppp/2n2n2/1B2p1BP/Pb6/1b3N2/3N1PP1/2qQK2R b - - 0 1', 'e2d1'),
(236, 'Qxd1', 36, '2026-04-12 11:52:06.000000', '9ddd14f0-f2f8-49b5-b8cd-dea57c7f2c6b', NULL, 'r2r2k1/pp3ppp/2n2n2/1B2p1BP/Pb6/1b3N2/3N1PP1/3qK2R w - - 0 1', 'c1d1'),
(237, 'h4', 1, '2026-04-12 11:52:39.000000', 'add5fbc3-353b-418b-a7f0-8e363afc3343', NULL, 'rnbqkbnr/pppppppp/8/8/7P/8/PPPPPPP1/RNBQKBNR b KQkq h3 0 1', 'h2h4'),
(238, 'd5', 2, '2026-04-12 11:52:40.000000', 'add5fbc3-353b-418b-a7f0-8e363afc3343', NULL, 'rnbqkbnr/ppp1pppp/8/3p4/7P/8/PPPPPPP1/RNBQKBNR w KQkq d6 0 1', 'd7d5'),
(239, 'g4', 1, '2026-04-12 11:52:54.000000', '9245b7d8-697d-4e37-9c6b-449aa44496e6', NULL, 'rnbqkbnr/pppppppp/8/8/6P1/8/PPPPPP1P/RNBQKBNR b KQkq g3 0 1', 'g2g4'),
(240, 'd5', 2, '2026-04-12 11:52:56.000000', '9245b7d8-697d-4e37-9c6b-449aa44496e6', NULL, 'rnbqkbnr/ppp1pppp/8/3p4/6P1/8/PPPPPP1P/RNBQKBNR w KQkq d6 0 1', 'd7d5'),
(241, 'e4', 3, '2026-04-12 11:52:57.000000', '9245b7d8-697d-4e37-9c6b-449aa44496e6', NULL, 'rnbqkbnr/ppp1pppp/8/3p4/4P1P1/8/PPPP1P1P/RNBQKBNR b KQkq e3 0 1', 'e2e4'),
(242, 'xe4', 4, '2026-04-12 11:52:58.000000', '9245b7d8-697d-4e37-9c6b-449aa44496e6', NULL, 'rnbqkbnr/ppp1pppp/8/8/4p1P1/8/PPPP1P1P/RNBQKBNR w KQkq - 0 1', 'd5e4'),
(243, 'h4', 1, '2026-04-12 11:53:24.000000', '94852d36-5c2f-4ca6-b2e6-d70fbe9d5e2a', NULL, 'rnbqkbnr/pppppppp/8/8/7P/8/PPPPPPP1/RNBQKBNR b KQkq h3 0 1', 'h2h4'),
(244, 'b5', 2, '2026-04-12 11:53:37.000000', '94852d36-5c2f-4ca6-b2e6-d70fbe9d5e2a', NULL, 'rnbqkbnr/p1pppppp/8/1p6/7P/8/PPPPPPP1/RNBQKBNR w KQkq b6 0 1', 'b7b5'),
(245, 'c4', 3, '2026-04-12 11:53:39.000000', '94852d36-5c2f-4ca6-b2e6-d70fbe9d5e2a', NULL, 'rnbqkbnr/p1pppppp/8/1p6/2P4P/8/PP1PPPP1/RNBQKBNR b KQkq c3 0 1', 'c2c4'),
(246, 'xc4', 4, '2026-04-12 11:53:42.000000', '94852d36-5c2f-4ca6-b2e6-d70fbe9d5e2a', NULL, 'rnbqkbnr/p1pppppp/8/8/2p4P/8/PP1PPPP1/RNBQKBNR w KQkq - 0 1', 'b5c4');

-- --------------------------------------------------------

--
-- Table structure for table `player`
--

CREATE TABLE `player` (
  `id` bigint(20) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `elo_rating` int(11) NOT NULL,
  `email` varchar(255) NOT NULL,
  `is_active` bit(1) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  `username` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `player`
--

INSERT INTO `player` (`id`, `created_at`, `elo_rating`, `email`, `is_active`, `password`, `role`, `username`) VALUES
(1, '2026-04-11 10:47:33.000000', 9999, 'adminK@gmail.com', b'1', '$2a$10$bKIXOqwzi4EuePByco.tX.bV7gEmKQ0oPdz3rIZj421OBjbYwEaMO', 'ROLE_ADMIN', 'admin'),
(2, NULL, 1201, 'phucvjppromax@gmail.com', b'1', '$2a$10$y.3CDp2Hbb75bX5E6bES1.xx37SP1ZRKYAtIMKOaI31tYQJGqpnH.', 'ROLE_USER', 'Felt'),
(3, NULL, 1199, 'Hungcter712@gmail.com', b'1', '$2a$10$2YJUzbhAJ0GZL.L1mPS7N.ZuxQQx20jG61IoVbpWgg7GqmeJQYwL.', 'ROLE_USER', 'bonken'),
(4, NULL, 1500, 'furina@chess.local', b'1', 'bot@123456', 'ROLE_USER', 'BOT_FURINA');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `games`
--
ALTER TABLE `games`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKof6gb88h93nx51mbuoomcltch` (`black_player_id`),
  ADD KEY `FKrr2c4h9rdplgb08hqmxi309k7` (`white_player_id`);

--
-- Indexes for table `move`
--
ALTER TABLE `move`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKrqrbpu4y6xmjy2ophgtcc5f1e` (`game_id`),
  ADD KEY `FKfyg07nm0qyopn03emleo4jhxi` (`player_id`);

--
-- Indexes for table `player`
--
ALTER TABLE `player`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKoivbimcon0iqmb8efpv723h08` (`email`),
  ADD UNIQUE KEY `UKo39xn8lmj05iew7d2tgw836jy` (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `move`
--
ALTER TABLE `move`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=247;

--
-- AUTO_INCREMENT for table `player`
--
ALTER TABLE `player`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `games`
--
ALTER TABLE `games`
  ADD CONSTRAINT `FKof6gb88h93nx51mbuoomcltch` FOREIGN KEY (`black_player_id`) REFERENCES `player` (`id`),
  ADD CONSTRAINT `FKrr2c4h9rdplgb08hqmxi309k7` FOREIGN KEY (`white_player_id`) REFERENCES `player` (`id`);

--
-- Constraints for table `move`
--
ALTER TABLE `move`
  ADD CONSTRAINT `FKfyg07nm0qyopn03emleo4jhxi` FOREIGN KEY (`player_id`) REFERENCES `player` (`id`),
  ADD CONSTRAINT `FKrqrbpu4y6xmjy2ophgtcc5f1e` FOREIGN KEY (`game_id`) REFERENCES `games` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
