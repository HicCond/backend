-- Quiz seed data: 20 geography questions.
-- Point distribution required by the specification:
--   3 questions x 100 = 300
--   4 questions x  75 = 300
--   5 questions x  40 = 200
--   8 questions x  25 = 200
--                       ----
--                       1000
-- SeedDataTest asserts this distribution, so a mistake here fails the build.

INSERT INTO question (id, question_text, points, correct_option_id) VALUES
('q01', 'Which mountain is the highest point of mainland Australia?', 100, 'a'),
('q02', 'Which sea is the only one on Earth with no land coastline?', 100, 'b'),
('q03', 'Apart from Vatican City, which sovereign state is completely surrounded by Italy?', 100, 'c'),
('q04', 'What is the capital of Australia?', 75, 'c'),
('q05', 'Which is the longest river in Asia?', 75, 'b'),
('q06', 'Which is the largest lake in the world by surface area?', 75, 'd'),
('q07', 'Which strait separates Asia from North America?', 75, 'a'),
('q08', 'Which is the largest island in the world?', 40, 'b'),
('q09', 'On which continent is the Atacama Desert located?', 40, 'c'),
('q10', 'Which is the highest mountain in Africa?', 40, 'b'),
('q11', 'What is the capital of Canada?', 40, 'd'),
('q12', 'Besides the Sea of Azov, which sea borders Ukraine?', 40, 'a'),
('q13', 'Which is the largest country in the world by area?', 25, 'c'),
('q14', 'What is the capital of France?', 25, 'b'),
('q15', 'Which is the highest mountain in the world?', 25, 'b'),
('q16', 'On which continent is most of Egypt located?', 25, 'c'),
('q17', 'Which is the largest ocean on Earth?', 25, 'c'),
('q18', 'What is the capital of Japan?', 25, 'c'),
('q19', 'Which country has a shape commonly compared to a boot?', 25, 'c'),
('q20', 'Which is the largest hot desert in the world?', 25, 'c');

INSERT INTO question_option (question_id, option_order, option_id, option_text) VALUES
('q01', 0, 'a', 'Mount Kosciuszko'),
('q01', 1, 'b', 'Uluru'),
('q01', 2, 'c', 'Mount Ossa'),
('q01', 3, 'd', 'Mount Bogong'),

('q02', 0, 'a', 'Coral Sea'),
('q02', 1, 'b', 'Sargasso Sea'),
('q02', 2, 'c', 'Tasman Sea'),
('q02', 3, 'd', 'Andaman Sea'),

('q03', 0, 'a', 'Monaco'),
('q03', 1, 'b', 'Andorra'),
('q03', 2, 'c', 'San Marino'),
('q03', 3, 'd', 'Liechtenstein'),

('q04', 0, 'a', 'Sydney'),
('q04', 1, 'b', 'Melbourne'),
('q04', 2, 'c', 'Canberra'),
('q04', 3, 'd', 'Perth'),

('q05', 0, 'a', 'Yellow River'),
('q05', 1, 'b', 'Yangtze'),
('q05', 2, 'c', 'Mekong'),
('q05', 3, 'd', 'Ob'),

('q06', 0, 'a', 'Lake Baikal'),
('q06', 1, 'b', 'Lake Victoria'),
('q06', 2, 'c', 'Lake Superior'),
('q06', 3, 'd', 'Caspian Sea'),

('q07', 0, 'a', 'Bering Strait'),
('q07', 1, 'b', 'Strait of Malacca'),
('q07', 2, 'c', 'Davis Strait'),
('q07', 3, 'd', 'Cook Strait'),

('q08', 0, 'a', 'New Guinea'),
('q08', 1, 'b', 'Greenland'),
('q08', 2, 'c', 'Borneo'),
('q08', 3, 'd', 'Madagascar'),

('q09', 0, 'a', 'Africa'),
('q09', 1, 'b', 'Asia'),
('q09', 2, 'c', 'South America'),
('q09', 3, 'd', 'Australia'),

('q10', 0, 'a', 'Mount Kenya'),
('q10', 1, 'b', 'Mount Kilimanjaro'),
('q10', 2, 'c', 'Mount Elgon'),
('q10', 3, 'd', 'Toubkal'),

('q11', 0, 'a', 'Toronto'),
('q11', 1, 'b', 'Vancouver'),
('q11', 2, 'c', 'Montreal'),
('q11', 3, 'd', 'Ottawa'),

('q12', 0, 'a', 'Black Sea'),
('q12', 1, 'b', 'Aegean Sea'),
('q12', 2, 'c', 'Adriatic Sea'),

('q13', 0, 'a', 'Canada'),
('q13', 1, 'b', 'China'),
('q13', 2, 'c', 'Russia'),
('q13', 3, 'd', 'United States'),

('q14', 0, 'a', 'Lyon'),
('q14', 1, 'b', 'Paris'),
('q14', 2, 'c', 'Marseille'),

('q15', 0, 'a', 'K2'),
('q15', 1, 'b', 'Mount Everest'),
('q15', 2, 'c', 'Kangchenjunga'),
('q15', 3, 'd', 'Mont Blanc'),

('q16', 0, 'a', 'Asia'),
('q16', 1, 'b', 'Europe'),
('q16', 2, 'c', 'Africa'),

('q17', 0, 'a', 'Atlantic Ocean'),
('q17', 1, 'b', 'Indian Ocean'),
('q17', 2, 'c', 'Pacific Ocean'),
('q17', 3, 'd', 'Arctic Ocean'),

('q18', 0, 'a', 'Beijing'),
('q18', 1, 'b', 'Seoul'),
('q18', 2, 'c', 'Tokyo'),
('q18', 3, 'd', 'Bangkok'),

('q19', 0, 'a', 'Greece'),
('q19', 1, 'b', 'Spain'),
('q19', 2, 'c', 'Italy'),

('q20', 0, 'a', 'Gobi'),
('q20', 1, 'b', 'Kalahari'),
('q20', 2, 'c', 'Sahara'),
('q20', 3, 'd', 'Atacama');
