-- -----------------------------------------------------
-- Table `FEEDGET`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `FEEDGET`.`user` (
  `user_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '유저 ID',
  `uuid` VARCHAR(32) NOT NULL COMMENT 'universally unique identifier, 외부 노출용 유저 ID',
  `real_name` VARCHAR(20) NOT NULL COMMENT '실명',
  `nickname` VARCHAR(20) NOT NULL COMMENT '닉네임',
  `email` VARCHAR(30) NOT NULL COMMENT '이메일',
  `cloud_msg_reg_id` VARCHAR(256) NULL DEFAULT NULL COMMENT 'cloud messaging 전송 ID',
  `user_grade_cd` VARCHAR(20) NOT NULL DEFAULT 'BRONZE' COMMENT '유저 등급',
  `use_version_code` INT(11) NOT NULL DEFAULT '0' COMMENT '유저가 사용하고 있는 앱의 버전 코드',
  `total_point_amount` DECIMAL(12,2) NOT NULL DEFAULT '0.00' COMMENT '획득한 포인트 총액',
  `current_point_amount` DECIMAL(12,2) NOT NULL DEFAULT '0.00' COMMENT '현재 보유 포인트 금액',
  `period_point_amount` DECIMAL(12,2) NOT NULL DEFAULT '0.00' COMMENT '일정 기간동안 획득한 포인트 금액',
  `feedback_writing_count` INT(11) NOT NULL DEFAULT '0' COMMENT '피드백 작성 횟수',
  `feedback_selection_count` INT(11) NOT NULL DEFAULT '0' COMMENT '작성한 피드백 채택 횟수',
  `oauth_token` VARCHAR(256) NULL DEFAULT NULL COMMENT 'OAuth Access Token',
  `oauth_type` VARCHAR(20) NULL DEFAULT NULL COMMENT '가입한 OAuth Type(카톡, FB)',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '최초 생성 날짜',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '최종 수정 날짜',
  PRIMARY KEY (`user_id`),
  UNIQUE INDEX `uuid_UNIQUE` (`uuid` ASC))
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8
  COMMENT = '유저';


-- -----------------------------------------------------
-- Table `FEEDGET`.`category`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `FEEDGET`.`category` (
  `category_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '카테고리 ID',
  `name` VARCHAR(20) NULL DEFAULT NULL COMMENT '카테고리 이름',
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '최초 생성 날짜',
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '최종 수정 날짜',
  PRIMARY KEY (`category_id`))
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8
  COMMENT = '카테고리';


-- -----------------------------------------------------
-- Table `FEEDGET`.`creation`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `FEEDGET`.`creation` (
  `creation_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '창작물 ID',
  `title` VARCHAR(50) NOT NULL COMMENT '창작물 제목',
  `description` TEXT NOT NULL COMMENT '창작물 설명',
  `status` VARCHAR(255) NOT NULL COMMENT '창작물 상태',
  `due_date` TIMESTAMP NOT NULL COMMENT '창작물 마감일',
  `reward_point` DECIMAL(12,2) NOT NULL DEFAULT '0.00' COMMENT '보상 포인트',
  `feedback_count` BIGINT(20) NOT NULL DEFAULT '0' COMMENT '창작물의 피드백 수',
  `writer_id` BIGINT(20) NULL DEFAULT NULL COMMENT '작성자 ID',
  `anonymity` CHAR(1) NULL DEFAULT NULL COMMENT '작성자 프로필 익명 여부',
  `category_id` BIGINT(20) NULL DEFAULT NULL COMMENT '카테고리 ID',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '최초 생성 날짜',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '최종 수정 날짜',
  PRIMARY KEY (`creation_id`),
  INDEX `fk_creation_to_category_id` (`category_id` ASC),
  INDEX `fk_creation_to_writer_id` (`writer_id` ASC),
  CONSTRAINT `fk_creation_to_category_id`
  FOREIGN KEY (`category_id`)
  REFERENCES `FEEDGET`.`category` (`category_id`),
  CONSTRAINT `fk_creation_to_writer_id`
  FOREIGN KEY (`writer_id`)
  REFERENCES `FEEDGET`.`user` (`user_id`))
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8
  COMMENT = '창작물';


-- -----------------------------------------------------
-- Table `FEEDGET`.`creation_attached_content`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `FEEDGET`.`creation_attached_content` (
  `creation_attached_content_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '창작물 첨부 컨텐츠 ID',
  `file_name` VARCHAR(52) NOT NULL COMMENT '파일 이름(중복 방지용)',
  `original_file_name` VARCHAR(255) NOT NULL COMMENT '파일 원본 이름',
  `size` INT(11) NULL DEFAULT '0' COMMENT '파일 사이즈',
  `type` VARCHAR(20) NULL DEFAULT NULL COMMENT '컨텐츠 종류(IMAGE, AUDIO)',
  `url` VARCHAR(255) NOT NULL COMMENT '첨부 컨텐츠 URL',
  `creation_id` BIGINT(20) NOT NULL COMMENT '창작물 ID',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '최초 생성 날짜',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '최종 수정 날짜',
  PRIMARY KEY (`creation_attached_content_id`),
  UNIQUE INDEX `UK_rc414co8w53ykqi3ml77gsr8n` (`file_name` ASC),
  INDEX `fk_content_to_creation_id` (`creation_id` ASC),
  CONSTRAINT `fk_content_to_creation_id`
  FOREIGN KEY (`creation_id`)
  REFERENCES `FEEDGET`.`creation` (`creation_id`))
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8
  COMMENT = '창작물에 첨부된 컨텐츠';


-- -----------------------------------------------------
-- Table `FEEDGET`.`feedback`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `FEEDGET`.`feedback` (
  `feedback_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '피드백 ID',
  `content` VARCHAR(255) NOT NULL COMMENT '피드백 내용',
  `creation_id` BIGINT(20) NOT NULL COMMENT '창작물 ID',
  `writer_id` BIGINT(20) NOT NULL COMMENT '작성자 ID',
  `anonymity` CHAR(1) NULL DEFAULT NULL COMMENT '작성자 프로필 익명 여부',
  `selection` CHAR(1) NULL DEFAULT NULL COMMENT '피드백 채택 여부',
  `selection_comment` VARCHAR(255) NOT NULL COMMENT '피드백 채택 의견',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '최초 생성 날짜',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '최종 수정 날짜',
  PRIMARY KEY (`feedback_id`),
  INDEX `fk_feedback_to_creation_id` (`creation_id` ASC),
  INDEX `fk_feedback_to_writer_id` (`writer_id` ASC),
  CONSTRAINT `fk_feedback_to_creation_id`
  FOREIGN KEY (`creation_id`)
  REFERENCES `FEEDGET`.`creation` (`creation_id`),
  CONSTRAINT `fk_feedback_to_writer_id`
  FOREIGN KEY (`writer_id`)
  REFERENCES `FEEDGET`.`user` (`user_id`))
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8
  COMMENT = '피드백';


-- -----------------------------------------------------
-- Table `FEEDGET`.`feedback_attached_content`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `FEEDGET`.`feedback_attached_content` (
  `feedback_attached_content_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '피드백 첨부 컨텐츠 ID',
  `file_name` VARCHAR(52) NOT NULL COMMENT '파일 이름(중복 방지용)',
  `original_file_name` VARCHAR(255) NOT NULL COMMENT '파일 원본 이름',
  `size` INT(11) NULL DEFAULT '0' COMMENT '파일 사이즈',
  `type` VARCHAR(20) NULL DEFAULT NULL COMMENT '컨텐츠 종류(IMAGE, AUDIO)',
  `url` VARCHAR(255) NOT NULL COMMENT '첨부 컨텐츠 URL',
  `feedback_id` BIGINT(20) NOT NULL COMMENT '피드백 ID',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '최초 생성 날짜',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '최종 수정 날짜',
  PRIMARY KEY (`feedback_attached_content_id`),
  UNIQUE INDEX `UK_9rcll0487n0aepesn41xs6eux` (`file_name` ASC),
  INDEX `fk_content_to_feedback_id` (`feedback_id` ASC),
  CONSTRAINT `fk_content_to_feedback_id`
  FOREIGN KEY (`feedback_id`)
  REFERENCES `FEEDGET`.`feedback` (`feedback_id`))
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8
  COMMENT = '피드백에 첨부된 컨텐츠';


-- -----------------------------------------------------
-- Table `FEEDGET`.`notification`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `FEEDGET`.`notification` (
  `notification_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '알림 ID',
  `description` VARCHAR(255) NOT NULL COMMENT '설명',
  `receiver_id` BIGINT(20) NULL DEFAULT NULL COMMENT '수신자 ID',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '최초 생성 날짜',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '최종 수정 날짜',
  PRIMARY KEY (`notification_id`))
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8
  COMMENT = '알림';


-- -----------------------------------------------------
-- Table `FEEDGET`.`point_history`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `FEEDGET`.`point_history` (
  `point_history_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '포인트 히스토리 ID',
  `giver_id` BIGINT(20) NULL DEFAULT NULL COMMENT '포인트 지급자 ID',
  `receiver_id` BIGINT(20) NULL DEFAULT NULL COMMENT '포인트 수여자 ID',
  `point_amount` DECIMAL(12,2) NOT NULL DEFAULT '0.00' COMMENT '포인트 금액',
  `type` VARCHAR(20) NULL DEFAULT NULL COMMENT '포인트 타입\nFEEDBACK_BASIC - 피드백 기본 포인트\nFEEDBACK_REWARD - 피드백 채택 보상 포인트',
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '최초 생성 날짜',
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '최종 수정 날짜',
  PRIMARY KEY (`point_history_id`))
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8
  COMMENT = '포인트 히스토리';
