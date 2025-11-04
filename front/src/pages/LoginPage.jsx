import React, { useState } from "react";
import { useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";
import { Card, Input, Button, Typography, Form, message } from "antd";
import { loginSuccess } from "../slice/authSlice";
import { loginUser } from "../api/login/authApi";
import { jwtDecode } from "jwt-decode";

const { Title, Text } = Typography;

export default function Login() {
  const [loading, setLoading] = useState(false);
  const [errorMsg, setErrorMsg] = useState(""); // ✅ 로그인 실패 이유 표시용
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const onFinish = async (values) => {
    setLoading(true);
    setErrorMsg(""); // 초기화
    try {
      const { user, token, refreshToken } = await loginUser(values);
      const decoded = jwtDecode(token);

      const userWithDept = {
        ...user,
        deptName: decoded.deptName || "소속 부서 미지정",
        deptCode: decoded.deptCode || "-",
        empName: decoded.empName || user.empName,
        email: decoded.email || user.email,
        username: decoded.username,
      };

      localStorage.setItem("token", token);
      localStorage.setItem("refreshToken", refreshToken);
      localStorage.setItem("user", JSON.stringify(userWithDept));
      dispatch(loginSuccess({ user: userWithDept, token }));

      message.success(`${userWithDept.deptName} ${userWithDept.empName}님 환영합니다 👋`);
      navigate("/main");
    } catch (err) {
      console.error("로그인 실패:", err);
      const error =
        err.response?.data?.message ||
        err.response?.data?.error ||
        err.message ||
        "로그인에 실패했습니다.";

      // ✅ 기존 모달에서 처리하던 오류 메시지 로직을 그대로 적용
      let displayMsg = "로그인에 실패했습니다.";
      if (error.includes("비밀번호")) {
        displayMsg = error.includes("남은 시도")
          ? `❌ ${error}`
          : "❌ 비밀번호가 일치하지 않습니다. 다시 확인해주세요.";
      } else if (error.includes("사용자를 찾을 수 없습니다")) {
        displayMsg = "❌ 아이디를 확인해주세요. 존재하지 않는 계정입니다.";
      } else if (
        error.includes("잠금") ||
        error.includes("잠겨") ||
        err.response?.data?.error === "LOGIN_FAILED"
      ) {
        displayMsg = "🔒 계정이 잠겼습니다. 관리자에게 문의하세요.";
      }

      setErrorMsg(displayMsg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={styles.container}>
      <Card style={styles.card} bordered={false} hoverable>
        <div style={styles.logoContainer}>
          <Title level={3} style={styles.title}>
            BizMate 로그인
          </Title>
          <Text type="secondary">기업을 위한 통합 관리 플랫폼</Text>
        </div>

        {/* 로그인 폼 */}
        <Form layout="vertical" onFinish={onFinish} style={{ marginTop: 24 }}>
          <Form.Item
            label="아이디"
            name="username"
            rules={[{ required: true, message: "아이디를 입력하세요." }]}
            validateStatus={errorMsg.includes("아이디") ? "error" : ""}
            help={errorMsg.includes("아이디") ? errorMsg : ""}
          >
            <Input size="large" placeholder="아이디" />
          </Form.Item>

          <Form.Item
            label="비밀번호"
            name="password"
            rules={[{ required: true, message: "비밀번호를 입력하세요." }]}
            validateStatus={
              errorMsg.includes("비밀번호") ||
              errorMsg.includes("남은 시도") ||
              errorMsg.includes("잠금")
                ? "error"
                : ""
            }
            help={
              errorMsg.includes("비밀번호") ||
              errorMsg.includes("남은 시도") ||
              errorMsg.includes("잠금")
                ? errorMsg
                : ""
            }
          >
            <Input.Password size="large" placeholder="비밀번호" />
          </Form.Item>

          <Button
            type="primary"
            htmlType="submit"
            size="large"
            block
            loading={loading}
            style={{ marginTop: 10, backgroundColor: "#1677ff" }}
          >
            로그인
          </Button>

          {/* ✅ 비밀번호 재설정 안내 */}
          <div style={{ textAlign: "right", marginTop: "8px" }}>
            <Button
              type="link"
              onClick={() => {
                message.info("비밀번호 재설정 페이지로 이동합니다.");
                navigate("/find-password"); // 새로운 페이지로 이동하도록 처리
              }}
            >
              비밀번호를 잊으셨나요?
            </Button>
          </div>
        </Form>

        {/* 푸터 영역 */}
        <div style={styles.footer}>
          <Text type="secondary" style={{ fontSize: 12 }}>
            ⓒ 2025 BizMate Inc. All rights reserved.
          </Text>
        </div>
      </Card>
    </div>
  );
}

const styles = {
  container: {
    height: "100vh",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    background: "linear-gradient(135deg, #f0f5ff 0%, #ffffff 100%)",
  },
  card: {
    width: 380,
    padding: "20px 30px",
    borderRadius: 16,
    boxShadow: "0 4px 16px rgba(0,0,0,0.1)",
    textAlign: "center",
  },
  logoContainer: {
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    marginBottom: 10,
  },
  logo: {
    width: 70,
    marginBottom: 8,
  },
  title: {
    marginBottom: 4,
    color: "#1677ff",
    fontWeight: 600,
  },
  footer: {
    marginTop: 24,
  },
};
