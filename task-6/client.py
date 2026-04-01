import json
import uuid
import logging
from urllib.request import Request, urlopen
from urllib.parse import urlencode
from urllib.error import URLError

logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(name)s - %(levelname)s - %(message)s')
logger = logging.getLogger("batch-client")

def start_etl_job(base_url="http://localhost:8080", run_id=None):
    trace_id = str(uuid.uuid4()).replace("-", "")
    span_id = str(uuid.uuid4().hex[:16])
    url = f"{base_url}/api/jobs/importProductJob"
    if run_id:
        url += "?" + urlencode({"runId": run_id})
    
    headers = {
        "X-Trace-Id": trace_id,
        "X-Span-Id": span_id
    }
    
    logger.info(f"Calling {url} | traceId={trace_id} | spanId={span_id}")
    
    req = Request(url, method="POST", headers=headers)
    try:
        with urlopen(req, timeout=30) as response:
            response_body = response.read().decode('utf-8')
            logger.info(f"Response status: {response.status} | body: {response_body}")
            return json.loads(response_body)
    except URLError as e:
        logger.error(f"Request failed: {e}", exc_info=True)
        return None

if __name__ == "__main__":
    start_etl_job(run_id="manual")