function loginApi(data) {
    return $axios({
      'url': '/user/login',
      'method': 'post',
      data
    })
  }

function loginoutApi() {
  return $axios({
    'url': '/user/loginout',
    'method': 'post',
  })
}

//随机验证码
function sendMsgApi(elem) {
    return axios({
        url: '/user/sendMsg',
        method: 'post',
        data: {...elem}
    })
}

  